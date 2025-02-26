import React from 'react'
import StockChart from './StockChart'
import './StockDetail.css'

function StockDetail({ stockData, dailyData }) {
    // Pokud chybí data, nevracíme nic
    if (!stockData || !dailyData || dailyData.length === 0) return null

    // Destrukturování dat
    const { companyName, open, close, high, low, date, volume } = stockData

    // Výpočet změny
    const differenceValue = close - open
    const percentChange = (differenceValue / open) * 100

    // Šipka nahoru/dolů a barva textu
    const arrowSymbol = differenceValue >= 0 ? '▲' : '▼'
    const changeColor = differenceValue >= 0 ? 'green' : 'red'

    // Příklad formátování pro CZ styl (můžeš změnit na en-US atd.)
    const formattedClose = close.toLocaleString('cs-CZ', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })
    const formattedDifference = differenceValue.toLocaleString('cs-CZ', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })
    const formattedPercentChange = percentChange.toLocaleString('cs-CZ', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })

    return (
        <div className="stock-detail">
            {/* Hlavička s názvem akcie */}
            <div className="stock-header">
                <h2 className="company-name">{companyName}</h2>
            </div>

            {/* Flex kontejner: vlevo graf + open/high/low, vpravo info */}
            <div className="stock-body">
                {/* LEVÁ ČÁST */}
                <div className="chart-section">
                    {/* Graf */}
                    <div className="chart-wrapper">
                        <StockChart dailyData={dailyData} lineColor={changeColor} />
                    </div>

                    {/* Pod grafem: open, high, low */}
                    <div className="chart-info-row">
                        <p>OPEN: {open}</p>
                        <p>HIGH: {high}</p>
                        <p>LOW: {low}</p>
                    </div>
                </div>

                {/* PRAVÁ ČÁST (close, change, date, volume) */}
                <div className="info-container">
                    {/* 1) Velké číslo (close) + menší měna (USD) */}
                    <div className="price-line">
                        <span className="main-price">{formattedClose}</span>
                        <span className="currency">USD</span>
                    </div>

                    {/* 2) Změna se šipkou a textem "dnes" */}
                    <div className="change-line">
                        <span className="arrow" style={{ color: changeColor }}>
                            {arrowSymbol}
                        </span>
                        <span className="change-value" style={{ color: changeColor }}>
                            {formattedDifference} ({formattedPercentChange}%)
                        </span>
                        <span className="time-label">dnes</span>
                    </div>

                    {/* 3) Doplňující informace: datum, volume, disclaimery... */}
                    <div className="extra-info">
                        <p>Zavřeno: {date}</p>
                        <p>Volume: {volume}</p>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default StockDetail
