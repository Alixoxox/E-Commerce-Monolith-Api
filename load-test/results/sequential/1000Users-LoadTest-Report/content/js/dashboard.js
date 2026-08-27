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

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.6438095238095238, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.561375, 500, 1500, "login Users"], "isController": false}, {"data": [1.0, 500, 1500, "Purchase Products Order"], "isController": false}, {"data": [0.1335, 500, 1500, "Mark wish"], "isController": false}, {"data": [0.144, 500, 1500, "See your marked wishes"], "isController": false}, {"data": [0.1225, 500, 1500, "give feedback"], "isController": false}, {"data": [0.636625, 500, 1500, "View History of Orders Bought"], "isController": false}, {"data": [0.7816666666666666, 500, 1500, "products by category"], "isController": false}, {"data": [0.7146666666666667, 500, 1500, "show all products"], "isController": false}, {"data": [0.7135833333333333, 500, 1500, "one product detail"], "isController": false}, {"data": [1.0, 500, 1500, "Create Users"], "isController": false}, {"data": [0.6980833333333333, 500, 1500, "show feedbacks of other people for this prod"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 42000, 0, 0.0, 1536.3916190476286, 0, 16696, 4.0, 61.900000000001455, 78.0, 98.0, 89.35029570693102, 7980.271145987347, 35.01912488565289], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["login Users", 4000, 0, 0.0, 2082.0525000000052, 61, 14357, 128.5, 6531.9, 7146.849999999999, 8492.829999999996, 10.785915751212066, 8.329146944451185, 4.576641009669574], "isController": false}, {"data": ["Purchase Products Order", 2000, 0, 0.0, 4.837499999999994, 2, 51, 5.0, 6.0, 7.0, 9.0, 20.04811547714515, 17.1630859375, 13.816714803027265], "isController": false}, {"data": ["Mark wish", 2000, 0, 0.0, 4187.390500000016, 2, 14298, 4252.5, 7276.800000000002, 8048.849999999999, 9444.230000000001, 11.77488769700859, 5.002027488475329, 5.1285155399080375], "isController": false}, {"data": ["See your marked wishes", 2000, 0, 0.0, 4140.610500000004, 1, 16696, 4328.0, 7196.9, 7960.299999999997, 9829.44, 11.776967784104626, 5.347554190613168, 4.887901668207486], "isController": false}, {"data": ["give feedback", 2000, 0, 0.0, 4359.618500000004, 6, 15903, 4477.0, 7606.600000000002, 8043.95, 9496.910000000002, 11.768236353258919, 5.194573077805694, 8.940722198277129], "isController": false}, {"data": ["View History of Orders Bought", 4000, 0, 0.0, 1168.0210000000018, 0, 5631, 2.0, 4184.400000000001, 4627.95, 5108.899999999998, 10.811512098082037, 6.525045818005762, 4.423851141695677], "isController": false}, {"data": ["products by category", 6000, 0, 0.0, 716.3466666666682, 0, 5593, 2.0, 3359.9000000000005, 4368.0, 4986.0, 16.186730118648732, 395.5648507010203, 5.321809055935944], "isController": false}, {"data": ["show all products", 6000, 0, 0.0, 1273.6028333333388, 2, 11563, 5.0, 5077.600000000002, 6510.95, 7831.99, 16.183455653285648, 49.41959553498458, 5.0520618396812935], "isController": false}, {"data": ["one product detail", 6000, 0, 0.0, 1401.7379999999996, 11, 12862, 26.0, 5784.300000000004, 6801.5999999999985, 8322.609999999991, 16.183673909759833, 4801.495080668871, 5.020521235677448], "isController": false}, {"data": ["Create Users", 2000, 0, 0.0, 79.2279999999998, 57, 433, 71.0, 97.0, 108.94999999999982, 250.60000000000036, 20.191618459177597, 15.595126658236666, 9.216231568838275], "isController": false}, {"data": ["show feedbacks of other people for this prod", 6000, 0, 0.0, 939.1098333333357, 2, 12584, 6.0, 3139.0, 3719.7999999999993, 6542.789999999995, 16.18773455352879, 4842.108673320725, 5.1482476102856864], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 42000, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
