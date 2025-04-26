import React from 'react';
import {CartesianGrid, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis} from 'recharts';
import moment from 'moment';

function StockChart({dailyData = [], lineColor}) {
    if (!dailyData || dailyData.length === 0) {
        return <p>No data available to display the chart.</p>;
    }

    return (
        <div style={{width: '100%', height: 500}}>
            <ResponsiveContainer>
                <LineChart data={dailyData}>
                    <CartesianGrid strokeDasharray="3 3"/>
                    <XAxis
                        dataKey="date"
                        tickFormatter={(dateStr) => moment(dateStr).format('DD.MM')} // kratší formát
                        label={{value: 'Date', position: 'insideBottomRight', offset: -5}}
                    />
                    <YAxis
                        label={{value: 'Stock Value', angle: -90, position: 'insideLeft'}}
                        domain={['auto', 'auto']}
                    />
                    <Tooltip
                        labelFormatter={(label) => `Date: ${moment(label).format('LL')}`}
                        formatter={(value, name) => [value?.toFixed(2), name.toUpperCase()]}
                    />
                    <Line
                        type="monotone"
                        dataKey="close"
                        stroke={lineColor || "#8884d8"}
                        strokeWidth={2}
                        dot={false}
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
}

export default StockChart;
