package eBaySellerParser;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.TimeFilter;
import com.ebay.sdk.call.GetSellerListCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.PaginationType;

import java.util.*;

public class SellerService {
    final String token = "AgAAAA**AQAAAA**aAAAAA**R1TsVA**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AGkIuoDJWHowidj6x9nY+seQ**lrgCAA**AAMAAA**wYDPchozhcBVlC/p7t3EUlf1emmWhWHpyUtBjLyGfW1J+wapCELriiK0WpkBQ8kVMISqr70JWGajFRZcBxYvMVSXGt0ZRvsOMS6EFQVo75M67PAW7Eq494HejyIxBmEX+/JO6xIwxbw1vh2ETa7pjcSdIOJZYp4/K2wmV5ZcuqfFfsstPcKcsnJpNBbPZIqxH+1LttvqIZSZcGF2zuIXuDfS55ee6laKNeRb/7bvMg941adyip2ROZGQq+98SnYjwzFKHA8ZennwYrgGriMKiTQVexxn26zp3WXNgcuD36LVYCVEZCuXCcQgAZuuWT7Iija2tm+flE7UjDlaOqmPPADNxysgNsOGaQztvxT+ncHGFZK3bIUx7+vhC1DNLNvpF76FRG0Y7WjpToq/cttdgAQYpu/Ngs3XylKLlOwg6IEHu0+4LXLJtqrkGVQ1G1bja2YRkqLq87CHvcOFW391vNHxHUoBx+5YWK674mbAVs2+XH8EQgcn9MAoypboS3TOdAMAGrzF0WOCLtddguhqaADttecKT2nwMWMgXmlFBr5Tmbe2pUn/N8Md8K8nFIpqk/ys1kkkKKigu+s3GtFWnZIOAGrZZrkyB5YBKBVUdHl3L7qNgQ9Vunbr6Dk0A11ItJ4lFWecj4imgCYb6xQtAlpnjIATpcYTPIR/BXfNA4v5sllmwQ0r6ycwLlPpxNruq+YMFcYXkWBpnUFtLBTFHW0rO2qgz9PUBMfGn1Yn2xozQb3TycfRpvOX02dA9QLG";
    private ApiContext apiContext;
    private List<String> eBayUserIDs;
    private Map<GregorianCalendar, GregorianCalendar> calendarPoints = new HashMap<GregorianCalendar, GregorianCalendar>();

    public List<String> geteBayUserIDs() {
        return eBayUserIDs;
    }

    public void seteBayUserIDs(List<String> eBayUserIDs) {
        this.eBayUserIDs = eBayUserIDs;
    }

    public SellerService() {
        apiContext = new ApiContext();
        ApiCredential cred = apiContext.getApiCredential();
        cred.seteBayToken(token);
        apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
}

    public Map<String, List<ItemType>> getAllItems() {
        Map<String, List<ItemType>> result = new HashMap<String, List<ItemType>>();

        for(String userId : eBayUserIDs){
           List<ItemType> itemTypeList = getItemListForUser(userId);
            result.put(userId, itemTypeList);
        }

        return result;
    }

    public List<ItemType> getItemListForUser(String userId){

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
        PaginationType paginationType = new PaginationType();

        for(int i = 0; i < 25 ; i++) {
            TimeFilter fromTime = new TimeFilter(calendarFrom, calendarTo);
            getSellerListCall.setStartTimeFilter(fromTime);
            try {
                ItemType[] items =  getSellerListCall.getSellerList();
                result.addAll(Arrays.asList(items));
                if(items.length == 0){
                    System.out.print(i + " is empty");
                }
            } catch (Exception e){
                System.out.print(e.toString());
            }

            calendarFrom.add(Calendar.DAY_OF_MONTH, -120);
            calendarTo.add(Calendar.DAY_OF_MONTH, -120);
        }



        return result;
    }


}
