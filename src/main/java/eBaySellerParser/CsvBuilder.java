package eBaySellerParser;

import com.ebay.soap.eBLBaseComponents.*;

import java.util.List;
import java.util.Map;

public class CsvBuilder
{
    public String buildCsv(Map<String, List<ItemType>> itemsMap)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(buildHeader());
        builder.append("\n");

        for (String userId : itemsMap.keySet())
        {
            List<ItemType> items = itemsMap.get(userId);
            if (builder.length() != 0)
            {
                builder.append("\t\t\t\t\t\t\t\t\n");
                builder.append("\t\t\t\t\t\t\t\t\n");
            }

            builder.append(userId + "\t\t\t\t\t\t\t");
            builder.append("\t\t\t\t\t\t\t\t\n");
            builder.append("\t\t\t\t\t\t\t\t\n");

            for(ItemType item : items){
                String itemCsvString = itemToCsv(item);
                builder.append(itemCsvString);
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    private String itemToCsv(ItemType item)
    {
        StringBuilder builder = new StringBuilder();
        Integer quantitySold = null;
        if (item.getSellingStatus() != null)
        {
            quantitySold = item.getSellingStatus().getQuantitySold();
        }

        Double shippingCost = null;
        String shippingCurrency = null;

        ShippingDetailsType shippingDetailsType = item.getShippingDetails();
        if (shippingDetailsType != null)
        {
            InternationalShippingServiceOptionsType[] options = shippingDetailsType.getInternationalShippingServiceOption();
            if (options != null && options.length > 0)
            {
                InternationalShippingServiceOptionsType firstOption = options[0];
                AmountType cost = firstOption.getShippingServiceCost();
                if (cost != null)
                {
                    shippingCost = cost.getValue();
                    CurrencyCodeType currencyCodeType = cost.getCurrencyID();
                    if (currencyCodeType != null)
                    {
                        shippingCurrency = currencyCodeType.name();
                    }
                }
            }
        }

        String shippingCostString = null;
        if (shippingCost != null)
        {
            shippingCostString = shippingCost.toString();
            if (shippingCurrency != null)
            {
                shippingCostString += " " + shippingCurrency;
            }
        }

        String pictureUrl = null;
        PictureDetailsType pictureDetailsType = item.getPictureDetails();
        if (pictureDetailsType != null)
        {
            String[] pictureUrls = pictureDetailsType.getPictureURL();
            if (pictureUrls != null)
            {
                pictureUrl = pictureUrls[0];
            }
        }

        Double itemCost = null;
        String itemCurrency = null;

        AmountType itemPrice = item.getStartPrice();
        if(itemPrice != null){
            itemCost = itemPrice.getValue();
            if(itemPrice.getCurrencyID() != null){
                itemCurrency = itemPrice.getCurrencyID().name();
            }
        }

        String itemCostString = null;
        if(itemCost != null){
            itemCostString = itemCost.toString();
            if(itemCurrency != null){
                itemCostString += " " + itemCurrency;
            }
        }

        Integer quantityInStock = null;
        if(item.getQuantity() != null){
            quantityInStock = item.getQuantity();
            if(quantitySold != null){
                quantityInStock -= quantitySold;
            }
        }

        builder.append(item.getItemID() + "\t");   //AuctionNumber
        builder.append("\t");           //PartNumber
        builder.append(item.getConditionDisplayName() + "\t"); //Condition
        builder.append(item.getTitle() != null ? item.getTitle() + "\t" : "\t");  // description
        builder.append(quantityInStock != null ? quantityInStock + "\t" : "\t");  // Quantity in stock
        builder.append(quantitySold != null ? quantitySold + "\t" : "\t");  // Quantity sold
        builder.append(itemCostString != null ? itemCostString + "\t" : "\t");  //Price
        builder.append(shippingCostString != null ? shippingCostString + "\t" : "\t");   //Shipping cost
        builder.append(pictureUrl != null ? pictureUrl : "");   //PictureLink
        return builder.toString();
    }

    private String buildHeader()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Auction number\t");
        builder.append("PartNumber\t");
        builder.append("Condition\t");
        builder.append("Description\t");
        builder.append("Quantity in stock\t");
        builder.append("Quantity sold\t");
        builder.append("Price\t");
        builder.append("Shipping cost\t");
        builder.append("Picture link");
        return builder.toString();
    }
}
