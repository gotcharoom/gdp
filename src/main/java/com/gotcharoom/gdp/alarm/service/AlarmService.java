package com.gotcharoom.gdp.alarm.service;

import com.gotcharoom.gdp.alarm.entity.AlarmTable;
import com.gotcharoom.gdp.alarm.repositoy.AlarmRepository;
import org.springframework.stereotype.Service;

@Service
public class AlarmService {

    AlarmRepository alarmRepository;

    public AlarmService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public AlarmTable testMethod() {
        System.out.println("뭘 봄");
        return null;
    }
}
