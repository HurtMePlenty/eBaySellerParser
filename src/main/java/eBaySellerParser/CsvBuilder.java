package eBaySellerParser;

import com.ebay.soap.eBLBaseComponents.*;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvBuilder
{
    private List<Seller> sellerList = new ArrayList<Seller>();
    private final String separator = "\t";

    public void complete()
    {
        StringBuilder builder = new StringBuilder();
        if (sellerList.isEmpty())
        {
            System.out.println("No sellers we found.");
            return;
        }

        //render first seller part with header
        builder.append(buildHeader(sellerList.get(0).userId));
        builder.append("\n");
        builder.append(renderItems(sellerList.get(0).itemList));

        //render all others
        int maxParamLength = getMaxParamsLength();
        for (Seller seller : sellerList)
        {
            if (seller == sellerList.get(0))
            { //first was already rendered
                continue;
            }
            builder.append(seller.userId);
            for (int i = 0; i < maxParamLength + Item.fieldCount(); i++)
            {
                builder.append(separator);
            }
            builder.append("\n");
            builder.append(renderItems(seller.itemList));
        }

        String result = builder.toString();
        try
        {
            File file = new File("./ebayoutput.txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(result.getBytes(Charset.forName("UTF-8")));
        }
        catch (Exception e)
        {
            System.out.println("Was unable to create output file. Error: " + e.toString());
        }

    }

    public String renderItems(List<Item> items)
    {
        int maxParamCount = getMaxParamsLength();

        StringBuilder result = new StringBuilder();
        for (Item item : items)
        {
            result.append(separator);//first column is for seller
            result.append(item.itemId).append(separator);   //AuctionNumber
            result.append(item.partNumber != null ? item.partNumber + separator : separator);           //PartNumber
            result.append(item.condition).append(separator); //Condition
            result.append(item.description != null ? item.description + separator : separator);  // description
            result.append(item.quantityInStock != null ? item.quantityInStock + separator : separator);  // Quantity in stock
            result.append(item.quantitySold != null ? item.quantitySold + separator : separator);  // Quantity sold
            result.append(item.price != null ? getFormattedPrice(item.price) + separator : separator);  //Price
            result.append(item.shippingCost != null ? getFormattedPrice(item.shippingCost) + separator : separator);   //Shipping cost
            result.append(item.pictureLink != null ? item.pictureLink + separator : separator);   //PictureLink
            result.append(item.isAuction);   //PictureLink

            for (int i = 0; i < maxParamCount; i++)
            {
                if (i < item.params.size())
                {
                    result.append(separator).append(item.params.get(i));
                } else
                {
                    result.append(separator);
                }
            }
            result.append("\n");
        }

        return result.toString();
    }

    private String getFormattedPrice(String price)
    {
        price = price.replace("USD", "").trim();
        try
        {
            Double priceDouble = Double.parseDouble(price);
            return String.format(Locale.ENGLISH, "%.2f", priceDouble);
            //return new DecimalFormat("#.00").format(priceDouble);
        }
        catch (Exception e)
        {
            //suppress
            return price;
        }
    }

    public void addItem(ItemType itemType, String userId)
    {
        Integer quantitySold = null;
        if (itemType.getSellingStatus() != null)
        {
            quantitySold = itemType.getSellingStatus().getQuantitySold();
        }

        Double shippingCost = null;
        String shippingCurrency = null;

        ShippingDetailsType shippingDetailsType = itemType.getShippingDetails();
        if (shippingDetailsType != null)
        {
            ShippingServiceOptionsType[] options = shippingDetailsType.getShippingServiceOptions();
            if (options != null && options.length > 0)
            {
                ShippingServiceOptionsType firstOption = options[0];
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
        PictureDetailsType pictureDetailsType = itemType.getPictureDetails();
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
        SellingStatusType sellingStatusType = itemType.getSellingStatus();
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
        if (itemType.getQuantity() != null)
        {
            quantityInStock = itemType.getQuantity();
            if (quantitySold != null)
            {
                quantityInStock -= quantitySold;
            }
        }

        Pattern pattern = Pattern.compile("^[A-Z0-9\\-]+$");
        //String partNumber = null;
        List<String> partNumbers = new ArrayList<String>();
        if (itemType.getTitle() != null)
        {
            String[] titleParts = itemType.getTitle().split(" ");
            int index = 0;
            for (String titlePart : titleParts)
            {
                if (titlePart.length() > 5 && (index == 0 || index == 1 || index == titleParts.length - 1))
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

        if (partNumbers.isEmpty())
        {
            partNumbers.add(null); //we failed to find a partNumber - add null to populate all other field once
        }

        StringBuilder builder = new StringBuilder();

        for (String partNumber : partNumbers)
        {

            if (partNumbers.indexOf(partNumber) > 0)
            {
                builder.append("\n");
            }

            //Item item = new Item();
            Item item = new Item();

            item.itemId = itemType.getItemID();
            item.partNumber = partNumber;
            item.condition = itemType.getConditionDisplayName();
            item.description = itemType.getTitle();
            item.quantityInStock = quantityInStock;
            item.quantitySold = quantitySold;
            item.price = itemCostString;
            item.shippingCost = shippingCostString;
            item.pictureLink = pictureUrl;
            item.isAuction = isAuction;

            for (String part : item.description.split(" "))
            {
                part = part.trim();
                if (!StringUtils.isEmpty(part))
                {
                    item.params.add(part);
                }
            }

            Seller seller = sellerById(userId);
            seller.itemList.add(item);

            /*builder.append(itemType.getItemID() + "\t");   //AuctionNumber
            builder.append(partNumber != null ? partNumber + "\t" : "\t");           //PartNumber
            builder.append(itemType.getConditionDisplayName() + "\t"); //Condition
            builder.append(itemType.getTitle() != null ? itemType.getTitle() + "\t" : "\t");  // description
            builder.append(quantityInStock != null ? quantityInStock + "\t" : "\t");  // Quantity in stock
            builder.append(quantitySold != null ? quantitySold + "\t" : "\t");  // Quantity sold
            builder.append(itemCostString != null ? itemCostString + "\t" : "\t");  //Price
            builder.append(shippingCostString != null ? shippingCostString + "\t" : "\t");   //Shipping cost
            builder.append(pictureUrl != null ? pictureUrl + "\t" : "\t");   //PictureLink
            builder.append(isAuction);   //PictureLink    */
        }

        //content.append(builder);
        //content.append("\n");
    }

    private String buildHeader(String firstSellerId)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(firstSellerId + separator); //sellerName in the first column
        builder.append("Auction number" + separator);
        builder.append("PartNumber" + separator);
        builder.append("Condition" + separator);
        builder.append("Description" + separator);
        builder.append("Quantity in stock" + separator);
        builder.append("Quantity sold" + separator);
        builder.append("Price" + separator);
        builder.append("Shipping cost" + separator);
        builder.append("Picture link" + separator);
        builder.append("isAuction" + separator);

        int additionalParams = getMaxParamsLength();
        for (int i = 0; i < additionalParams; i++)
        {
            builder.append(separator);
        }


        return builder.toString();
    }

    private int getMaxParamsLength()
    {
        int result = 0;
        for (Seller seller : sellerList)
        {
            List<Item> itemList = seller.itemList;
            for (Item item : itemList)
            {
                int paramLength = item.params.size();
                if (paramLength > result)
                {
                    result = paramLength;
                }
            }
        }
        return result;
    }

    private Seller sellerById(String userId)
    {
        for (Seller seller : sellerList)
        {
            if (seller.userId.equals(userId))
            {
                return seller;
            }
        }

        Seller newSeller = new Seller(userId);
        sellerList.add(newSeller);
        return newSeller;
    }


    private static class Item
    {
        public static int fieldCount()
        {
            return 10;
        }

        public String itemId;
        public String partNumber;
        public String condition;
        public String description;
        public Integer quantityInStock;
        public Integer quantitySold;
        public String price;
        public String shippingCost;
        public String pictureLink;
        public boolean isAuction;
        public List<String> params = new ArrayList<String>();
    }

    private static class Seller
    {
        private Seller(String userId)
        {
            this.userId = userId;
        }

        public String userId;
        public List<Item> itemList = new ArrayList<Item>();
    }
}
