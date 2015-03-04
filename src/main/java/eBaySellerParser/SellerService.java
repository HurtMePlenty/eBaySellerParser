package eBaySellerParser;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.TimeFilter;
import com.ebay.sdk.call.GetSellerListCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.PaginationType;

import java.util.*;

public class SellerService
{
    final String token = "AgAAAA**AQAAAA**aAAAAA**R1TsVA**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AGkIuoDJWHowidj6x9nY+seQ**lrgCAA**AAMAAA**wYDPchozhcBVlC/p7t3EUlf1emmWhWHpyUtBjLyGfW1J+wapCELriiK0WpkBQ8kVMISqr70JWGajFRZcBxYvMVSXGt0ZRvsOMS6EFQVo75M67PAW7Eq494HejyIxBmEX+/JO6xIwxbw1vh2ETa7pjcSdIOJZYp4/K2wmV5ZcuqfFfsstPcKcsnJpNBbPZIqxH+1LttvqIZSZcGF2zuIXuDfS55ee6laKNeRb/7bvMg941adyip2ROZGQq+98SnYjwzFKHA8ZennwYrgGriMKiTQVexxn26zp3WXNgcuD36LVYCVEZCuXCcQgAZuuWT7Iija2tm+flE7UjDlaOqmPPADNxysgNsOGaQztvxT+ncHGFZK3bIUx7+vhC1DNLNvpF76FRG0Y7WjpToq/cttdgAQYpu/Ngs3XylKLlOwg6IEHu0+4LXLJtqrkGVQ1G1bja2YRkqLq87CHvcOFW391vNHxHUoBx+5YWK674mbAVs2+XH8EQgcn9MAoypboS3TOdAMAGrzF0WOCLtddguhqaADttecKT2nwMWMgXmlFBr5Tmbe2pUn/N8Md8K8nFIpqk/ys1kkkKKigu+s3GtFWnZIOAGrZZrkyB5YBKBVUdHl3L7qNgQ9Vunbr6Dk0A11ItJ4lFWecj4imgCYb6xQtAlpnjIATpcYTPIR/BXfNA4v5sllmwQ0r6ycwLlPpxNruq+YMFcYXkWBpnUFtLBTFHW0rO2qgz9PUBMfGn1Yn2xozQb3TycfRpvOX02dA9QLG";
    private ApiContext apiContext;
    private int periodsWith120Days;
    private ProgressReporter progressReporter;
    private int itemsPerPage = 200;
    private CsvBuilder csvBuilder;

    public void setPeriodsWith120Days(int periodsWith120Days)
    {
        this.periodsWith120Days = periodsWith120Days;
    }

    public void setProgressReporter(ProgressReporter progressReporter)
    {
        this.progressReporter = progressReporter;
    }

    public void setCsvBuilder(CsvBuilder csvBuilder) {
        this.csvBuilder = csvBuilder;
    }

    public SellerService()
    {
        apiContext = new ApiContext();
        ApiCredential cred = apiContext.getApiCredential();
        cred.seteBayToken(token);
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
    }

    public void getAllItems(List<String> eBayUserIDs)
    {
        progressReporter.reportMessage("Getting items for sellers: " + eBayUserIDs.toString());

        for (String userId : eBayUserIDs)
        {
            userId = userId.trim();
            if (userId.isEmpty())
            {
                continue;
            }
            progressReporter.reportMessage("Getting data for seller " + userId);
            csvBuilder.newSeller(userId);
            getItemListForUser(userId);
        }
        csvBuilder.complete();
    }

    private GetSellerListCall createGetSellerListCall(String userId, Calendar from, Calendar to, int currentPage){
        GetSellerListCall getSellerListCall = new GetSellerListCall(apiContext);
        getSellerListCall.setUserID(userId);

        DetailLevelCodeType[] details = {DetailLevelCodeType.ITEM_RETURN_DESCRIPTION};
        getSellerListCall.setDetailLevel(details);
        TimeFilter fromTime = new TimeFilter(from, to);
        getSellerListCall.setStartTimeFilter(fromTime);

        PaginationType paginationType = new PaginationType();
        paginationType.setEntriesPerPage(itemsPerPage);
        paginationType.setPageNumber(currentPage);
        getSellerListCall.setPagination(paginationType);

        return getSellerListCall;
    }

    public void getItemListForUser(String userId)
    {

        //make several calls to retrive data

        Date now = new Date();

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(now);

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(now);
        calendarFrom.add(Calendar.DAY_OF_MONTH, -120);

        int totalCount = 0;

        for (int i = 0; i < periodsWith120Days; i++)
        {
            int currentPage = 1;

            boolean isLastPage = false;
            while (!isLastPage)
            {
                try
                {
                    System.gc();
                    GetSellerListCall getSellerListCall = createGetSellerListCall(userId, calendarFrom, calendarTo, currentPage);
                    progressReporter.reportMessage(String.format("Getting data for seller = [%s] period= [%d] page = [%d]", userId, i, currentPage));
                    ItemType[] items = getSellerListCall.getSellerList();
                    for(ItemType item : items){
                        csvBuilder.addItem(item);
                    }

                    totalCount += items.length;

                    progressReporter.reportMessage(String.format("Received %d items. Total for seller = %d", items.length, totalCount));
                    if (items.length == 0)
                    {
                        System.out.print(i + " is empty");
                    }
                    if (items.length < itemsPerPage)
                    {
                        isLastPage = true;
                    } else
                    {
                        currentPage++;
                    }
                }
                catch (Exception e)
                {
                    isLastPage = true;
                    progressReporter.reportMessage("Error occurred: " + e.toString());
                }
            }

            calendarFrom.add(Calendar.DAY_OF_MONTH, -120);
            calendarTo.add(Calendar.DAY_OF_MONTH, -120);
        }


    }


}
