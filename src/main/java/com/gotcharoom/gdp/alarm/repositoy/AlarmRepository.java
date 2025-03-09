package com.gotcharoom.gdp.alarm.repositoy;

import com.gotcharoom.gdp.alarm.entity.AlarmTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmTable, String> {

}
