package eBaySellerParser;

import com.ebay.soap.eBLBaseComponents.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                builder.append("\t\t\t\t\t\t\t\t\t\n");
                builder.append("\t\t\t\t\t\t\t\t\t\n");
            }

            builder.append(userId + "\t\t\t\t\t\t\t\t");
            builder.append("\t\t\t\t\t\t\t\t\t\n");
            builder.append("\t\t\t\t\t\t\t\t\t\n");

            for (ItemType item : items)
            {
                String itemCsvString = itemToCsv(item);
                builder.append(itemCsvString);
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    private String itemToCsv(ItemType item)
    {
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
        boolean isAuction = false;
        SellingStatusType sellingStatusType = item.getSellingStatus();
        if (sellingStatusType != null)
        {
            AmountType itemPrice = sellingStatusType.getCurrentPrice();
            if (itemPrice != null)
            {
                itemCost = itemPrice.getValue();
                if (itemPrice.getCurrencyID() != null)
                {
                    itemCurrency = itemPrice.getCurrencyID().name();
                }
            }

            AmountType bidIncrement = sellingStatusType.getBidIncrement();
            if (bidIncrement != null)
            {
                if (bidIncrement.getValue() > 0)
                {
                    isAuction = true;
                }
            }
        }

        String itemCostString = null;
        if (itemCost != null)
        {
            itemCostString = itemCost.toString();
            if (itemCurrency != null)
            {
                itemCostString += " " + itemCurrency;
            }
        }

        Integer quantityInStock = null;
        if (item.getQuantity() != null)
        {
            quantityInStock = item.getQuantity();
            if (quantitySold != null)
            {
                quantityInStock -= quantitySold;
            }
        }

        Pattern pattern = Pattern.compile("^[A-Z0-9\\-]+$");
        //String partNumber = null;
        List<String> partNumbers = new ArrayList<String>();
        if (item.getTitle() != null)
        {
            String[] titleParts = item.getTitle().split(" ");
            int index = 0;
            for (String titlePart : titleParts)
            {
                if (titlePart.length() > 6 && (index == 0 || index == 1 || index == titlePart.length() - 1))
                {
                    Matcher matcher = pattern.matcher(titlePart);
                    if (matcher.matches())
                    {
                        partNumbers.add(titlePart);
                        //break;
                    }
                }
                index++;
            }
        }

        if(partNumbers.isEmpty()){
            partNumbers.add(null); //we failed to find a partNumber - add null to populate all other field once
        }

        StringBuilder builder = new StringBuilder();

        for(String partNumber : partNumbers) {

            if(partNumbers.indexOf(partNumber) > 0){
                builder.append("\n");
            }

            builder.append(item.getItemID() + "\t");   //AuctionNumber
            builder.append(partNumber != null ? partNumber + "\t" : "\t");           //PartNumber
            builder.append(item.getConditionDisplayName() + "\t"); //Condition
            builder.append(item.getTitle() != null ? item.getTitle() + "\t" : "\t");  // description
            builder.append(quantityInStock != null ? quantityInStock + "\t" : "\t");  // Quantity in stock
            builder.append(quantitySold != null ? quantitySold + "\t" : "\t");  // Quantity sold
            builder.append(itemCostString != null ? itemCostString + "\t" : "\t");  //Price
            builder.append(shippingCostString != null ? shippingCostString + "\t" : "\t");   //Shipping cost
            builder.append(pictureUrl != null ? pictureUrl + "\t" : "\t");   //PictureLink
            builder.append(isAuction);   //PictureLink
        }

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
        builder.append("Picture link\t");
        builder.append("isAuction");
        return builder.toString();
    }
}
