/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 98.62380952380953, "KoPercent": 1.3761904761904762};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.05447619047619048, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.02925, 500, 1500, "login Users"], "isController": false}, {"data": [0.0015, 500, 1500, "Purchase Products Order"], "isController": false}, {"data": [0.1495, 500, 1500, "Mark wish"], "isController": false}, {"data": [0.5495, 500, 1500, "See your marked wishes"], "isController": false}, {"data": [0.001, 500, 1500, "give feedback"], "isController": false}, {"data": [0.04875, 500, 1500, "View History of Orders Bought"], "isController": false}, {"data": [0.020666666666666667, 500, 1500, "products by category"], "isController": false}, {"data": [0.04133333333333333, 500, 1500, "show all products"], "isController": false}, {"data": [0.006, 500, 1500, "one product detail"], "isController": false}, {"data": [0.0, 500, 1500, "Create Users"], "isController": false}, {"data": [0.0275, 500, 1500, "show feedbacks of other people for this prod"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 21000, 289, 1.3761904761904762, 16858.952523809672, 1, 59728, 16670.5, 35995.30000000001, 44140.95, 50169.91000000002, 142.05506324832578, 12808.073885544205, 52.71143242026314], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["login Users", 2000, 17, 0.85, 12276.156499999986, 2, 23416, 12660.5, 21720.3, 22344.85, 22715.96, 59.86590038314176, 47.15240595441212, 18.607040931438576], "isController": false}, {"data": ["Purchase Products Order", 1000, 5, 0.5, 33298.681999999935, 24, 57135, 37229.5, 49692.3, 50962.65, 53988.71, 9.381567096967876, 8.022513342347455, 6.451366542517262], "isController": false}, {"data": ["Mark wish", 1000, 12, 1.2, 8536.882999999996, 97, 54641, 2440.5, 40351.1, 45747.9, 51032.0, 10.562673623947694, 4.4802652947514074, 4.5716984383087045], "isController": false}, {"data": ["See your marked wishes", 1000, 12, 1.2, 3671.85300000001, 7, 46739, 637.0, 14688.999999999995, 28517.499999999993, 34629.99, 13.699193117525379, 6.439450208570215, 5.648295049796567], "isController": false}, {"data": ["give feedback", 1000, 12, 1.2, 31009.307000000015, 14, 59728, 28064.0, 47437.5, 49704.95, 52681.340000000004, 9.004060831434977, 3.9668515500490718, 6.817735931717705], "isController": false}, {"data": ["View History of Orders Bought", 2000, 17, 0.85, 19160.84150000001, 33, 55494, 17639.0, 42996.5, 46524.149999999994, 50754.96, 17.99775028121485, 12.100622187851519, 7.329504710348706], "isController": false}, {"data": ["products by category", 3000, 0, 0.0, 16913.752333333334, 3, 41939, 17586.0, 20712.0, 21560.849999999988, 31908.909999999996, 25.036929470969678, 1071.0377885506123, 8.199260250014605], "isController": false}, {"data": ["show all products", 3000, 11, 0.36666666666666664, 13548.554666666658, 2, 28341, 14336.5, 22057.0, 22506.9, 22774.839999999997, 59.07372400756144, 180.2891597255041, 18.338276142584277], "isController": false}, {"data": ["one product detail", 3000, 0, 0.0, 15819.907999999996, 32, 31894, 15161.0, 22431.6, 23316.649999999998, 25859.729999999996, 38.062371539496056, 10813.691728888072, 11.758683275869725], "isController": false}, {"data": ["Create Users", 1000, 203, 20.3, 11377.769999999999, 1, 22939, 11602.0, 20082.9, 21160.8, 22017.97, 35.71683691692264, 40.61586848837417, 9.838628261393671], "isController": false}, {"data": ["show feedbacks of other people for this prod", 3000, 0, 0.0, 21474.288999999975, 8, 57549, 20231.0, 42859.7, 45697.95, 50483.829999999994, 21.21130704073985, 6344.770076060212, 6.7185641248745], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["403", 58, 20.069204152249135, 0.2761904761904762], "isController": false}, {"data": ["Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 231, 79.93079584775087, 1.1], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 21000, 289, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 231, "403", 58, "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["login Users", 2000, 17, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 17, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["Purchase Products Order", 1000, 5, "403", 5, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["Mark wish", 1000, 12, "403", 12, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["See your marked wishes", 1000, 12, "403", 12, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["give feedback", 1000, 12, "403", 12, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["View History of Orders Bought", 2000, 17, "403", 17, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["show all products", 3000, 11, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 11, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["Create Users", 1000, 203, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 203, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
