package com.gmacster.azure.servicebus.testing.cancellation;

import com.microsoft.azure.servicebus.TopicClient;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App {

  public static void main(String[] args)
      throws ServiceBusException, InterruptedException {

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd;

    try {
      cmd = parser.parse(getOptions(), args);
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      return;
    }

    String connectionString = cmd.getOptionValue("s");

    ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(connectionString);

    TopicClient topicClient = new TopicClient(connectionStringBuilder);

    for (String sequenceNumber : cmd.getOptionValues("n")) {
      try {
        topicClient.cancelScheduledMessageAsync(Long.parseLong(sequenceNumber)).get();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }

    topicClient.close();
    System.exit(0);
  }

  private static Options getOptions() {
    Options options = new Options();

    Option connectionString = new Option("s", true, "ServiceBus Connection String");
    connectionString.setRequired(true);
    options.addOption(connectionString);

    Option sequenceNumbers = new Option("n", "A list of sequence numbers to try and cancel");
    sequenceNumbers.setRequired(true);
    sequenceNumbers.setArgs(Option.UNLIMITED_VALUES);
    sequenceNumbers.setType(Long.class);
    options.addOption(sequenceNumbers);

    return options;
  }
}