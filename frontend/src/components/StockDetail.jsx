import React from 'react'
import StockChart from './StockChart'
import './StockDetail.css'

function StockDetail({stockData, dailyData, favourites, onToggleFavourite}) {
    if (!stockData || !dailyData || dailyData.length === 0) return null

    const {symbol, companyName, open, close, high, low, date, volume} = stockData

    const differenceValue = close - open
    const percentChange = (differenceValue / open) * 100
    const arrowSymbol = differenceValue >= 0 ? '▲' : '▼'
    const changeColor = differenceValue >= 0 ? 'green' : 'red'

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

    // Zjistíme, zda je akcie mezi oblíbenými
    const isFavourite = favourites.some(f => f.symbol === symbol)

    return (
        <div className="stock-detail">
            <div className="stock-header">
                <h2 className="company-name">{companyName}</h2>
                <button
                    className="favourite-button"
                    onClick={() => onToggleFavourite(symbol, companyName)}
                    title={isFavourite ? "Remove from favourites" : "Add to favourites"}
                >
                    <span className="favourite-text">
                        {isFavourite ? "Remove from favourites" : "Add to favourites"}
                    </span>
                    <span className="favourite-star" style={{color: 'yellow'}}>
                        {isFavourite ? "★" : "☆"}
                    </span>
                </button>
            </div>

            <div className="stock-body">
                <div className="chart-section">
                    <div className="chart-wrapper">
                        <StockChart dailyData={dailyData} lineColor={changeColor}/>
                    </div>
                    <div className="chart-info-row">
                        <p>OPEN: {open}</p>
                        <p>HIGH: {high}</p>
                        <p>LOW: {low}</p>
                    </div>
                </div>
                <div className="info-container">
                    <div className="price-line">
                        <span className="main-price">{formattedClose}</span>
                        <span className="currency">USD</span>
                    </div>
                    <div className="change-line">
                        <span className="arrow" style={{color: changeColor}}>
                            {arrowSymbol}
                        </span>
                        <span className="change-value" style={{color: changeColor}}>
                            {formattedDifference} ({formattedPercentChange}%)
                        </span>
                        <span className="time-label">dnes</span>
                    </div>
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
