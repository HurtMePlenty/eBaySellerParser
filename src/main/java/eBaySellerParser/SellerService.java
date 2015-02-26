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

    public void setPeriodsWith120Days(int periodsWith120Days)
    {
        this.periodsWith120Days = periodsWith120Days;
    }

    public void setProgressReporter(ProgressReporter progressReporter)
    {
        this.progressReporter = progressReporter;
    }

    public SellerService()
    {
        apiContext = new ApiContext();
        ApiCredential cred = apiContext.getApiCredential();
        cred.seteBayToken(token);
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
    }

    public Map<String, List<ItemType>> getAllItems(List<String> eBayUserIDs)
    {
        progressReporter.reportMessage("Getting items for sellers: " + eBayUserIDs.toString());
        Map<String, List<ItemType>> result = new HashMap<String, List<ItemType>>();

        for (String userId : eBayUserIDs)
        {
            progressReporter.reportMessage("Getting data for seller " + userId);
            List<ItemType> itemTypeList = getItemListForUser(userId);
            result.put(userId, itemTypeList);
        }

        return result;
    }

    public List<ItemType> getItemListForUser(String userId)
    {

        //make several calls to retrive data

        List<ItemType> result = new ArrayList<ItemType>();

        GetSellerListCall getSellerListCall = new GetSellerListCall(apiContext);
        getSellerListCall.setUserID(userId);

        Date now = new Date();

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(now);

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(now);
        calendarFrom.add(Calendar.DAY_OF_MONTH, -120);

        DetailLevelCodeType[] details = {DetailLevelCodeType.RETURN_ALL};
        getSellerListCall.setDetailLevel(details);
        PaginationType paginationType = new PaginationType();
        getSellerListCall.setPagination(paginationType);
        paginationType.setEntriesPerPage(200);

        for (int i = 0; i < periodsWith120Days; i++)
        {
            TimeFilter fromTime = new TimeFilter(calendarFrom, calendarTo);
            getSellerListCall.setStartTimeFilter(fromTime);
            int currentPage = 1;
            paginationType.setPageNumber(currentPage);

            boolean isLastPage = false;
            while (!isLastPage)
            {
                try
                {
                    progressReporter.reportMessage(String.format("Getting data for seller = [%s] period= [%d] page = [%d]", userId, i, currentPage));
                    ItemType[] items = getSellerListCall.getSellerList();
                    result.addAll(Arrays.asList(items));
                    progressReporter.reportMessage(String.format("Received %d items. Total for seller = %d", items.length, result.size()));
                    if (items.length == 0)
                    {
                        System.out.print(i + " is empty");
                    }
                    if (items.length < 200)
                    {
                        isLastPage = true;
                    } else
                    {
                        paginationType.setPageNumber(++currentPage);
                    }
                }
                catch (Exception e)
                {
                    progressReporter.reportMessage("Error occurred: " + e.toString());
                }
            }

            calendarFrom.add(Calendar.DAY_OF_MONTH, -120);
            calendarTo.add(Calendar.DAY_OF_MONTH, -120);
        }


        return result;
    }


}
