package com.github.ffrancoc.foca.lib;

import com.github.ffrancoc.foca.task.AsyncSqlManager;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLRunner {
    private String[] querys;
    private Connection conn;
    private String sqlQuery;
    private TabPane tpResult;
    private Label resultInfo;
    private TableView tvGlobalMsgList;

    public SQLRunner(Connection conn, String sqlQuery, TabPane tbResult, Label resultInfo, TableView tvGlobalMsgList) {
        this.conn = conn;
        this.sqlQuery = sqlQuery;
        this.tpResult = tbResult;
        this.resultInfo = resultInfo;
        this.tvGlobalMsgList = tvGlobalMsgList;
        init();
    }

    private void init() {
        querys = sqlQuery.split("(?<=;)\\s*");
        for(int x = 0; x < querys.length; x++) {
            AsyncSqlManager asyncSqlManager = new AsyncSqlManager(conn, querys[x], tpResult, resultInfo, tvGlobalMsgList);

            ExecutorService service = Executors.newFixedThreadPool(1);
            service.execute(asyncSqlManager);
            service.shutdown();
        }
    }

}
