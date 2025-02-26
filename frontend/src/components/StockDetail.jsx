import React from 'react'
import StockChart from './StockChart'
import './StockDetail.css'

function StockDetail({ stockData, dailyData }) {
    if (!stockData || !dailyData || dailyData.length === 0) return null

    const { companyName, open, close, high, low, date, volume } = stockData
    const percentChange = ((close - open) / open) * 100
    const changeColor = percentChange >= 0 ? 'green' : 'red'

    return (
        <div className="stock-detail">
            <div className="stock-header">
                <h2 className="company-name">{companyName}</h2>
            </div>

            <div className="chart-wrapper">
                <StockChart dailyData={dailyData} />
            </div>

            <div className="info-container">
                <div className="info-left">
                    <p><strong>HIGH:</strong> {high}</p>
                    <p><strong>OPEN:</strong> {open}</p>
                    <p><strong>LOW:</strong> {low}</p>
                </div>
                <div className="info-right">
                    <p><strong>CLOSE:</strong> {close}</p>
                    <p style={{ color: changeColor }}><strong>CHANGE:</strong> {percentChange.toFixed(2)}%</p>
                    <p><strong>DATE:</strong> {date}</p>
                    <p><strong>VOLUME:</strong> {volume}</p>
                </div>
            </div>
        </div>
    )
}

export default StockDetail
