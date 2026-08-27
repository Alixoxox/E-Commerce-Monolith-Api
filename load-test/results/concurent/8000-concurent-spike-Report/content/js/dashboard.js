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

    var data = {"OkPercent": 59.33571428571429, "KoPercent": 40.66428571428571};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.03133333333333333, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.01, 500, 1500, "login Users"], "isController": false}, {"data": [0.0, 500, 1500, "Purchase Products Order"], "isController": false}, {"data": [0.1775, 500, 1500, "Mark wish"], "isController": false}, {"data": [0.266, 500, 1500, "See your marked wishes"], "isController": false}, {"data": [5.0E-4, 500, 1500, "give feedback"], "isController": false}, {"data": [0.04175, 500, 1500, "View History of Orders Bought"], "isController": false}, {"data": [0.0015, 500, 1500, "products by category"], "isController": false}, {"data": [0.017333333333333333, 500, 1500, "show all products"], "isController": false}, {"data": [6.666666666666666E-4, 500, 1500, "one product detail"], "isController": false}, {"data": [0.0, 500, 1500, "Create Users"], "isController": false}, {"data": [0.017333333333333333, 500, 1500, "show feedbacks of other people for this prod"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 42000, 17079, 40.66428571428571, 19469.467619047726, 0, 90240, 21464.5, 44084.50000000001, 49561.95, 69986.98000000001, 197.30259171047254, 10121.820789498099, 41.19976124037562], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["login Users", 4000, 9, 0.225, 34746.92124999998, 14, 87395, 36562.0, 65521.200000000004, 68177.0, 70825.51999999999, 39.89149513323759, 30.966561967074554, 12.476912407626257], "isController": false}, {"data": ["Purchase Products Order", 2000, 1209, 60.45, 9568.734500000017, 1, 46391, 6.0, 30410.4, 34264.5, 36670.490000000005, 14.206462519800258, 26.797134156952996, 3.874923640263956], "isController": false}, {"data": ["Mark wish", 2000, 978, 48.9, 7051.669499999981, 1, 47419, 243.0, 30568.800000000003, 33658.95, 37316.380000000005, 14.096320155623374, 20.724171742076106, 3.1417380035910374], "isController": false}, {"data": ["See your marked wishes", 2000, 921, 46.05, 5338.873000000007, 1, 46148, 151.0, 27240.100000000006, 30578.55, 36109.98, 14.09731375686363, 20.24341903251898, 3.1605447796766075], "isController": false}, {"data": ["give feedback", 2000, 1164, 58.2, 12936.343500000006, 1, 57121, 11.5, 34810.8, 35879.9, 40477.33, 13.986111790991545, 23.448611037052707, 4.454706359397618], "isController": false}, {"data": ["View History of Orders Bought", 4000, 2171, 54.275, 7272.347749999994, 1, 47382, 13.0, 29990.9, 33561.8, 36869.56999999999, 28.190256038000463, 49.431201019430134, 5.283284818930461], "isController": false}, {"data": ["products by category", 6000, 3809, 63.483333333333334, 10191.07566666666, 0, 69492, 6.0, 27108.9, 30277.75, 68734.99, 33.97335356635279, 1684.6285272568923, 4.067092595790701], "isController": false}, {"data": ["show all products", 6000, 639, 10.65, 38165.7171666667, 1, 83260, 44080.5, 69930.1, 70758.95, 72492.65999999999, 48.06536890170632, 144.2982456140351, 12.92010258952175], "isController": false}, {"data": ["one product detail", 6000, 2563, 42.71666666666667, 27615.824833333296, 1, 90240, 28189.0, 69538.8, 70720.95, 72851.66999999997, 39.02312119931059, 6842.386756731488, 5.89827109280999], "isController": false}, {"data": ["Create Users", 2000, 6, 0.3, 35759.06050000001, 18, 57906, 34854.0, 50416.3, 53822.54999999998, 56608.95, 32.93048374880627, 25.629290610490006, 11.36428099890506], "isController": false}, {"data": ["show feedbacks of other people for this prod", 6000, 3610, 60.166666666666664, 8749.249333333351, 1, 48356, 7.0, 30144.800000000003, 31744.8, 36470.649999999994, 38.71467286101433, 4919.6287424183765, 4.933688520292941], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["403", 20, 0.11710287487557819, 0.047619047619047616], "isController": false}, {"data": ["Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 17059, 99.88289712512442, 40.61666666666667], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 42000, 17079, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 17059, "403", 20, "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["login Users", 4000, 9, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 9, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["Purchase Products Order", 2000, 1209, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 1205, "403", 4, "", "", "", "", "", ""], "isController": false}, {"data": ["Mark wish", 2000, 978, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 975, "403", 3, "", "", "", "", "", ""], "isController": false}, {"data": ["See your marked wishes", 2000, 921, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 918, "403", 3, "", "", "", "", "", ""], "isController": false}, {"data": ["give feedback", 2000, 1164, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 1161, "403", 3, "", "", "", "", "", ""], "isController": false}, {"data": ["View History of Orders Bought", 4000, 2171, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 2164, "403", 7, "", "", "", "", "", ""], "isController": false}, {"data": ["products by category", 6000, 3809, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 3809, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["show all products", 6000, 639, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 639, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["one product detail", 6000, 2563, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 2563, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["Create Users", 2000, 6, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 6, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["show feedbacks of other people for this prod", 6000, 3610, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException/Non HTTP response message: Connect to localhost:8080 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused: connect", 3610, "", "", "", "", "", "", "", ""], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
