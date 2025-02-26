function StockDetail({ stockData, favourites, onToggleFavourite }) {
    if (!stockData) return null

    const { symbol, companyName, open, close, high, low, date } = stockData

    const percentChange = ((close - open) / open) * 100
    const changeColor = percentChange >= 0 ? 'green' : 'red'

    const isFavourite = favourites.includes(symbol)

    return (
        <div className="stock-detail">
            <div className="stock-header">
                <h2 className="company-name">{companyName}</h2>
                <span
                    className="favourite-star"
                    style={{ color: isFavourite ? 'gold' : 'gray', cursor: 'pointer' }}
                    onClick={() => onToggleFavourite(symbol)}
                >
          ★
        </span>
            </div>

            {/* Placeholder graf – orámovaný barvou changeColor */}
            <div className="stock-graph" style={{ border: `2px solid ${changeColor}` }}>
                <p style={{ textAlign: 'center', marginTop: '80px', color: '#ccc' }}>
                    Graph placeholder
                </p>
            </div>

            <div className="stock-values">
                <p>HIGH: {high}</p>
                <p>OPEN: {open}</p>
                <p>LOW: {low}</p>
                <p>CLOSE: {close}</p>
                <p>
                    CHANGE:{' '}
                    <span style={{ color: changeColor }}>
            {percentChange.toFixed(2)}%
          </span>
                </p>
                {/* Datum z daily data */}
                <p>DATE: {date}</p>
            </div>
        </div>
    )
}

export default StockDetail
