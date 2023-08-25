package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) {


        String name = args[0];
//  String name = "/home/namesirname/tickets.json";
        StringBuilder list;
        try {
            list = ReadFile.toImport(name);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String res = list.toString();


        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<Ticket>>>() {
        }.getType();
        Map<String, List<Ticket>> myMap = gson.fromJson(res, type);

        List<Ticket> collect = new ArrayList<>();
        try {
            collect = myMap.values().stream().flatMap(r -> r.stream()).collect(Collectors.toList());
        } catch (NullPointerException e) {
            e.getMessage();
        }

//        System.out.println(collect.toString() + " Всего " + collect.size());

        Map<String, List<Long>> airline = new TreeMap<>();
        List<Integer> prices = new ArrayList<>();
        for (Ticket it : collect
        ) {
            if ((it.origin.equals("VVO") && it.destination.equals("TLV"))
                    || (it.origin.equals("TLV") && it.destination.equals("VVO"))) {
//                System.out.println(it.carrier);
                Date departure;
                Date arrival;
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.UK);
                try {
                    departure = formatter.parse(it.departure_date + " " + it.departure_time);
                    arrival = formatter.parse(it.arrival_date + " " + it.arrival_time);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                prices.add(it.price);
                long diffInMillies = Math.abs(arrival.getTime() - departure.getTime());
                long diff = TimeUnit.MILLISECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                long diff1 = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
//                System.out.println(diff + " " + diffInMillies + " hours = " + diff1 + " " + departure + " " + arrival);


                if (airline.containsKey(it.carrier)) {
                    airline.get(it.carrier).add(diff);

                }
                if (!airline.containsKey(it.carrier)) {
                    List<Long> timeDiffList = new ArrayList<>();
                    airline.put(it.carrier, timeDiffList);
                    airline.get(it.carrier).add(diff);

                }

            }

        }
//        System.out.println(airline.toString());
        for (Map.Entry<String, List<Long>> entry : airline.entrySet()) {

            Long min = Collections.min(entry.getValue());
//            System.out.println(min);
            long hours = TimeUnit.MILLISECONDS.toHours(min)%24;
//            System.out.println(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(min)%60;
//            System.out.println(minutes);
            long seconds= TimeUnit.MILLISECONDS.toSeconds(min)%60;
//            System.out.println(seconds);
//            System.out.format("Минимальное время полета между городами Владивосток и Тель-Авив для " +
//                            " авиаперевозчика %s, : %.2f время в часах \n", entry.getKey(),
//                    TimeUnit.MINUTES.convert(Collections.min(entry.getValue()), TimeUnit.MILLISECONDS) / 60.0);

            System.out.format("Минимальное время полета между городами Владивосток и Тель-Авив для " +
                            " авиаперевозчика %s - %d : %d :%d часов минут секунд \n", entry.getKey(),
                    hours, minutes, seconds);

        }

        double average = prices.stream().mapToDouble(d -> d).average().orElse(0);
//        System.out.println( prices);
        System.out.println("Средняя цена " + average);


        Collections.sort(prices);
//        System.out.println(prices);
        Integer median;
        if (prices.size() % 2 == 0)
            median = (prices.get(prices.size() / 2) + prices.get(prices.size() / 2 - 1)) / 2;
        else
            median = prices.get(prices.size() / 2);
        System.out.println("Медиана " + (double) median);
        System.out.format("Разницу между средней ценой  и медианой для полета между городами Владивосток и Тель-Авив %.0f",
                Math.abs(median - average));
    }
}
