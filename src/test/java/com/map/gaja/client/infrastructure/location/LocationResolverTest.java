//package com.map.gaja.client.infrastructure.location;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.map.gaja.client.infrastructure.file.excel.ClientExcelData;
//import com.map.gaja.client.infrastructure.location.LocationResolver;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//class LocationResolverTest {
//
//    @Test
//    void convertCoordinate() {
//        LocationResolver resolver = new LocationResolver(new ObjectMapper());
//        long startTime = System.nanoTime();
//
//        List<ClientExcelData> addresses = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            addresses.add(new ClientExcelData("", "", "서울특별시 강남구", "", null, null));
//        }
//        resolver.convertCoordinate(addresses);
//
//        long executionTime = (System.nanoTime() - startTime) / 1000000;
//        System.out.println("Time: " + executionTime + " ms");
//
//        addresses.stream()
//                .forEach(i -> System.out.println(i.getLocation().getLatitude()));
//    }
//}