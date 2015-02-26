package eBaySellerParser;

public class ConsoleProgressReporter implements ProgressReporter
{

    @Override
    public void reportMessage(String message)
    {
        System.out.println(message);
    }
}
