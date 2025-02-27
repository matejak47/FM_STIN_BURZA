import React from 'react'
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer
}
from 'recharts'
import moment from 'moment'

function StockChart({ dailyData, lineColor }) {
    return (
        <div style={{ width: '100%', height: 500 }}>
            <ResponsiveContainer>
                <LineChart data={dailyData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis
                        dataKey="date"
                        tickFormatter={(dateStr) => moment(dateStr).format('YYYY')}
                        label={{ value: 'Days', position: 'insideBottomRight', offset: -5 }}
                    />
                    <YAxis label={{ value: 'Stock Value', angle: -90, position: 'insideLeft' }} />
                    <Tooltip
                        labelFormatter={(label) => `Date: ${moment(label).format('LL')}`}
                        formatter={(value, name) => [value.toFixed(2), name.toUpperCase()]}
                    />
                    <Line
                        type="monotone"
                        dataKey="close"
                        stroke={lineColor || "#82ca9d"}
                        strokeWidth={2}
                        dot={false}
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    )
}

export default StockChart
