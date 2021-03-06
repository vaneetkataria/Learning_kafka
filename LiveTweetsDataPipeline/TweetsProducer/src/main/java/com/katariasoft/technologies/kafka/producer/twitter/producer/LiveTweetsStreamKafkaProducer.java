package com.katariasoft.technologies.kafka.producer.twitter.producer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.katariasoft.technologies.kafka.producer.exception.KafkaProducerException;
import com.katariasoft.technologies.kafka.producer.twitter.producer.config.TweetsGethrerKafkaProducerConfig;
import com.katariasoft.technologies.kafka.producer.twitter.producer.helper.LiveTweetsStreamKafkaForwarder;

public class LiveTweetsStreamKafkaProducer {

	private static String TOPIC = "twitterstream";
	private static Properties kafkaConfigs;
	private static List<String> tweetTerms;

	static {
		// kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic new_test_topic
		// Creating properties for kafka Producer.
		// interested tweet terms
		tweetTerms = Arrays.asList("is");
		// kafka configs
		kafkaConfigs = TweetsGethrerKafkaProducerConfig.get();

	}

	private LiveTweetsStreamKafkaForwarder streamForwarder;

	public LiveTweetsStreamKafkaProducer(Properties kafkaConfigs, String kafkaTopic, List<String> tweetTerms) {
		try {
			streamForwarder = new LiveTweetsStreamKafkaForwarder(kafkaConfigs, kafkaTopic, tweetTerms);
			addShutDownHook();
		} catch (Exception e) {
			System.out.println(
					"Exception occured while creating  LiveTwitterStreamKafkaProducer throwing exception with cause.");
			throw KafkaProducerException.instance(
					"Exception occured while creating  LiveTwitterStreamKafkaProducer throwing exception with cause.",
					e);
		}
	}

	public void produce() {
		streamForwarder.forward();
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("Going to stop application with Kafka producer and twitter client.");
				streamForwarder.stop();
				System.out.println("Application exited successfully.");
			}
		}));

	}

	public static void main(String args[]) {
		try {
			new LiveTweetsStreamKafkaProducer(kafkaConfigs, TOPIC, tweetTerms).produce();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
