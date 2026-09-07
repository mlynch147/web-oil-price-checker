/* ==========================================================================
   Oil Price Checker - Dashboard UI (v2) behaviour
   Vanilla JS (no jQuery). Uses the same REST endpoints as the original page:
     GET /prices/{litres}
     GET /fourteen-day-comparison
     GET /weekly-comparison
     GET /six-month-comparison
   ========================================================================== */
(function () {
    'use strict';

    var SUPPLIER_PLACEHOLDERS = [
        'Craigs Fuels', 'Moores Fuels', 'McGinleys Oils', 'Campsie Fuels',
        'Scotts Fuels', 'Springtown Fuels', 'Nicholls Oils', 'Bellarena Fuels'
    ];

    var PALETTE = ['#1d4e89', '#f2a541', '#157f52', '#b3261e', '#7b4ea3',
        '#0f8b8d', '#c8553d', '#3d5a80', '#8a5b06', '#2f7d32'];

    // ---------------------------------------------------------------- utils

    function $(selector, root) {
        return (root || document).querySelector(selector);
    }

    function $$(selector, root) {
        return Array.prototype.slice.call((root || document).querySelectorAll(selector));
    }

    function getJson(url) {
        return fetch(url, { headers: { Accept: 'application/json' } }).then(function (res) {
            if (!res.ok) {
                throw new Error('Request failed with status ' + res.status);
            }
            return res.json();
        });
    }

    /** Parses "£312.50" / "312.50" / "(62.5 ppl)" into a number, or null. */
    function toNumber(value) {
        if (value === null || value === undefined) {
            return null;
        }
        var match = String(value).match(/-?\d+(\.\d+)?/);
        return match ? parseFloat(match[0]) : null;
    }

    function formatCurrency(value) {
        return '£' + Number(value).toFixed(2);
    }

    function setLoading(button, isLoading) {
        if (!button) {
            return;
        }
        button.classList.toggle('is-loading', isLoading);
        button.disabled = isLoading;
    }

    function setStatus(el, message, state) {
        if (!el) {
            return;
        }
        el.textContent = message;
        el.setAttribute('data-state', state || 'idle');
    }

    function toast(message, isError) {
        var stack = $('#toastStack');
        if (!stack) {
            return;
        }
        var node = document.createElement('div');
        node.className = 'toast' + (isError ? ' toast--error' : '');
        node.setAttribute('role', isError ? 'alert' : 'status');
        node.textContent = message;
        stack.appendChild(node);
        window.setTimeout(function () {
            node.remove();
        }, 5000);
    }

    function stampUpdated() {
        var el = $('#lastUpdated');
        if (el) {
            el.textContent = new Date().toLocaleString('en-GB', {
                day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
            });
        }
    }

    // ------------------------------------------------------------ Highcharts

    function applyChartTheme() {
        if (typeof Highcharts === 'undefined') {
            return;
        }
        Highcharts.setOptions({
            colors: PALETTE,
            chart: {
                style: {
                    fontFamily: '"Inter", "Segoe UI", -apple-system, BlinkMacSystemFont, Roboto, Arial, sans-serif'
                },
                backgroundColor: 'transparent',
                spacing: [12, 12, 8, 4]
            },
            title: { text: null },
            credits: { enabled: false },
            legend: {
                itemStyle: { fontWeight: '500', color: '#5c6672', fontSize: '12px' },
                itemHoverStyle: { color: '#16202b' }
            },
            xAxis: {
                lineColor: '#dbe2ea',
                tickColor: '#dbe2ea',
                labels: { style: { color: '#5c6672', fontSize: '11.5px' } }
            },
            yAxis: {
                gridLineColor: '#eef1f5',
                labels: { style: { color: '#5c6672', fontSize: '11.5px' } },
                title: { style: { color: '#5c6672', fontSize: '12px' } }
            },
            tooltip: {
                backgroundColor: 'rgba(255,255,255,0.98)',
                borderColor: '#dbe2ea',
                borderRadius: 8,
                shadow: false,
                style: { color: '#16202b', fontSize: '12.5px' }
            },
            lang: { noData: 'No data available yet' }
        });
    }

    function trendChart(containerId, data, yTitle) {
        var series = data.map(function (item) {
            return {
                name: item.supplierName,
                data: item.priceDataPoints.map(function (point) {
                    return [new Date(point.date).getTime(), point.value];
                })
            };
        });

        Highcharts.chart(containerId, {
            chart: { type: 'line' },
            xAxis: { type: 'datetime', title: { text: null } },
            yAxis: { title: { text: yTitle } },
            plotOptions: {
                line: { marker: { enabled: false, symbol: 'circle', radius: 3 }, lineWidth: 2 },
                series: { states: { inactive: { opacity: 0.25 } } }
            },
            tooltip: {
                shared: false,
                xDateFormat: '%e %b %Y',
                pointFormatter: function () {
                    return '<span style="color:' + this.series.color + '">\u25CF</span> '
                        + this.series.name + ': <b>' + formatCurrency(this.y) + '</b><br/>';
                }
            },
            series: series
        });
    }

    // ------------------------------------------------------------ price table

    function renderSkeletonRows() {
        var tbody = $('#priceTableBody');
        tbody.innerHTML = '';
        SUPPLIER_PLACEHOLDERS.forEach(function (name, index) {
            var tr = document.createElement('tr');
            tr.innerHTML =
                '<td><span class="rank">' + (index + 1) + '</span>' + name + '</td>' +
                '<td class="num"><span class="skeleton"></span></td>' +
                '<td class="num"><span class="skeleton"></span></td>';
            tbody.appendChild(tr);
        });
    }

    function renderPriceRows(prices) {
        var tbody = $('#priceTableBody');
        tbody.innerHTML = '';

        var available = prices.filter(function (p) {
            return toNumber(p.price) !== null;
        });
        var cheapest = available.length ? toNumber(available[0].price) : null;

        prices.forEach(function (price, index) {
            var value = toNumber(price.price);
            var tr = document.createElement('tr');
            tr.className = 'cell-enter';
            tr.style.animationDelay = (index * 45) + 'ms';

            if (value === null) {
                tr.classList.add('is-unavailable');
            } else if (cheapest !== null && value === cheapest) {
                tr.classList.add('is-best');
            }

            var supplierCell = document.createElement('td');
            var rank = document.createElement('span');
            rank.className = 'rank';
            rank.textContent = String(index + 1);
            supplierCell.appendChild(rank);
            supplierCell.appendChild(document.createTextNode(price.supplierName));
            if (tr.classList.contains('is-best')) {
                var badge = document.createElement('span');
                badge.className = 'badge badge--best';
                badge.textContent = 'Best price';
                supplierCell.appendChild(badge);
            }

            var costCell = document.createElement('td');
            costCell.className = 'num';
            costCell.textContent = value === null ? 'Unavailable' : price.price;

            var pplCell = document.createElement('td');
            pplCell.className = 'num';
            pplCell.textContent = toNumber(price.pencePerLitre) === null
                ? '—'
                : String(price.pencePerLitre).replace(/[()]/g, '');

            tr.appendChild(supplierCell);
            tr.appendChild(costCell);
            tr.appendChild(pplCell);
            tbody.appendChild(tr);
        });
    }

    function renderStats(prices, litres) {
        var values = prices
            .map(function (p) { return toNumber(p.price); })
            .filter(function (v) { return v !== null; });

        var bestValue = $('#statBestValue');
        var bestMeta = $('#statBestMeta');
        var spreadValue = $('#statSpreadValue');
        var spreadMeta = $('#statSpreadMeta');
        var avgValue = $('#statAverageValue');
        var avgMeta = $('#statAverageMeta');

        if (!values.length) {
            bestValue.textContent = '—';
            bestMeta.textContent = 'No supplier prices available';
            spreadValue.textContent = '—';
            spreadMeta.textContent = 'Nothing to compare';
            avgValue.textContent = '—';
            avgMeta.textContent = 'Nothing to compare';
            return;
        }

        var min = Math.min.apply(null, values);
        var max = Math.max.apply(null, values);
        var avg = values.reduce(function (a, b) { return a + b; }, 0) / values.length;
        var best = prices.find(function (p) { return toNumber(p.price) === min; });

        bestValue.textContent = formatCurrency(min);
        bestMeta.textContent = best.supplierName + ' · ' + ((min / litres) * 100).toFixed(1) + ' ppl';

        spreadValue.textContent = formatCurrency(max - min);
        spreadMeta.textContent = 'Between cheapest and dearest of ' + values.length + ' suppliers';

        avgValue.textContent = formatCurrency(avg);
        avgMeta.textContent = 'Average across ' + values.length + ' suppliers for ' + litres + ' litres';
    }

    function selectedLitres() {
        var checked = $('input[name="litre"]:checked');
        return checked ? parseInt(checked.value, 10) : 500;
    }

    function loadPrices() {
        var litres = selectedLitres();
        var button = $('#fetchPricesBtn');

        $('#volumeHeading').textContent = litres.toLocaleString('en-GB') + ' litres of home heating oil';
        setLoading(button, true);
        renderSkeletonRows();
        setStatus($('#tableStatus'), 'Fetching live prices from suppliers…', 'loading');

        return getJson('/prices/' + encodeURIComponent(litres))
            .then(function (prices) {
                renderPriceRows(prices);
                renderStats(prices, litres);
                stampUpdated();
                setStatus($('#tableStatus'),
                    'Showing ' + prices.length + ' suppliers, cheapest first.', 'idle');
            })
            .catch(function (error) {
                setStatus($('#tableStatus'), 'Could not load prices. ' + error.message, 'error');
                toast('Failed to fetch supplier prices.', true);
            })
            .finally(function () {
                setLoading(button, false);
            });
    }

    // --------------------------------------------------------------- charts

    function loadFourteenDay() {
        var button = $('#twoWeekBtn');
        setLoading(button, true);
        setStatus($('#twoWeekStatus'), 'Loading 14 day history…', 'loading');

        return getJson('/fourteen-day-comparison')
            .then(function (data) {
                trendChart('twoWeekChart', data, 'Cost for 500 litres (£)');
                setStatus($('#twoWeekStatus'), 'Daily prices for 500 litres over the last 14 days.', 'idle');
            })
            .catch(function (error) {
                setStatus($('#twoWeekStatus'), 'Could not load chart. ' + error.message, 'error');
            })
            .finally(function () {
                setLoading(button, false);
            });
    }

    function loadSixMonths() {
        var button = $('#sixMonthBtn');
        setLoading(button, true);
        setStatus($('#sixMonthStatus'), 'Loading six month history…', 'loading');

        return getJson('/six-month-comparison')
            .then(function (data) {
                trendChart('sixMonthChart', data, 'Cost for 500 litres (£)');
                setStatus($('#sixMonthStatus'), 'Prices for 500 litres over the last six months.', 'idle');
            })
            .catch(function (error) {
                setStatus($('#sixMonthStatus'), 'Could not load chart. ' + error.message, 'error');
            })
            .finally(function () {
                setLoading(button, false);
            });
    }

    function loadWeeklyComparison() {
        var button = $('#weeklyBtn');
        setLoading(button, true);
        setStatus($('#weeklyStatus'), 'Loading week on week movement…', 'loading');

        return getJson('/weekly-comparison')
            .then(function (response) {
                var categories = [];
                var points = [];

                response.forEach(function (item) {
                    categories.push(item.supplierName);
                    var noChange = item.priceDifference === 0;
                    points.push({
                        y: item.priceDifference,
                        color: noChange ? '#c3ceda' : (item.priceDifference > 0 ? '#b3261e' : '#157f52'),
                        name: item.supplierName,
                        today: item.todaysPrice,
                        weekAgo: item.weekOldPrice,
                        dataLabels: noChange ? {
                            enabled: true,
                            format: 'No change',
                            style: { color: '#5c6672', fontSize: '11px', fontWeight: '600', textOutline: 'none' }
                        } : { enabled: false }
                    });
                });

                Highcharts.chart('weeklyChart', {
                    chart: { type: 'column' },
                    xAxis: { categories: categories, title: { text: null } },
                    yAxis: {
                        title: { text: 'Change vs 7 days ago (£)' },
                        plotLines: [{ color: '#5c6672', width: 1, value: 0, zIndex: 3 }]
                    },
                    legend: { enabled: false },
                    plotOptions: { column: { borderRadius: 3, borderWidth: 0, pointPadding: 0.08 } },
                    tooltip: {
                        formatter: function () {
                            return '<b>' + this.point.name + '</b><br/>'
                                + 'Today: ' + formatCurrency(this.point.today) + '<br/>'
                                + '7 days ago: ' + formatCurrency(this.point.weekAgo) + '<br/>'
                                + 'Change: <b>' + (this.y > 0 ? '+' : '') + formatCurrency(this.y) + '</b>';
                        }
                    },
                    series: [{ name: 'Price difference', data: points }]
                });

                var risers = points.filter(function (p) { return p.y > 0; }).length;
                var fallers = points.filter(function (p) { return p.y < 0; }).length;
                setStatus($('#weeklyStatus'),
                    fallers + ' supplier(s) cheaper, ' + risers + ' dearer than seven days ago.', 'idle');
            })
            .catch(function (error) {
                setStatus($('#weeklyStatus'), 'Could not load chart. ' + error.message, 'error');
            })
            .finally(function () {
                setLoading(button, false);
            });
    }

    // ------------------------------------------------------------------ init

    function bind() {
        $('#fetchPricesBtn').addEventListener('click', loadPrices);
        $('#twoWeekBtn').addEventListener('click', loadFourteenDay);
        $('#weeklyBtn').addEventListener('click', loadWeeklyComparison);
        $('#sixMonthBtn').addEventListener('click', loadSixMonths);

        $('#refreshAllBtn').addEventListener('click', function () {
            loadPrices();
            loadFourteenDay();
            loadWeeklyComparison();
            loadSixMonths();
        });

        $$('input[name="litre"]').forEach(function (input) {
            input.addEventListener('change', function () {
                $('#volumeHeading').textContent =
                    selectedLitres().toLocaleString('en-GB') + ' litres of home heating oil';
            });
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        applyChartTheme();
        bind();
        renderSkeletonRows();
        loadFourteenDay();
        loadWeeklyComparison();
        loadSixMonths();
    });
}());

