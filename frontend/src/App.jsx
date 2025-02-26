import React, { useState } from 'react'
import { fetchStockData } from './utils/fetchStockData'
import StockDetail from './components/StockDetail'
import SearchBar from './components/SearchBar'
import FavouritesTable from './components/FavouritesTable'

function App() {
    const [selectedSymbol, setSelectedSymbol] = useState('')
    const [selectedName, setSelectedName] = useState('')
    const [dailyData, setDailyData] = useState(null)
    const [selectedStockData, setSelectedStockData] = useState(null)
    const [favourites, setFavourites] = useState([])


    const handleSelectSymbol = (symbol, name) => {
        setSelectedSymbol(symbol)
        setSelectedName(name)
    }

    const handleShowStock = async () => {
        if (!selectedSymbol) return

        try {
            const allData = await fetchStockData(selectedSymbol)
            if (!allData || allData.length === 0) {
                setSelectedStockData(null)
                setDailyData(null)
                return
            }

            // Data seřadíme chronologicky (od nejstaršího po nejnovější)
            allData.sort((a, b) => new Date(a.date) - new Date(b.date))
            setDailyData(allData)

            // Pro zobrazení detailu vezmeme poslední záznam (aktuální den)
            const lastEntry = allData[allData.length - 1]
            const detail = {
                symbol: selectedSymbol,
                companyName: selectedName,
                open: lastEntry.open,
                close: lastEntry.close,
                high: lastEntry.high,
                low: lastEntry.low,
                date: lastEntry.date,
                volume: lastEntry.volume

            }
            setSelectedStockData(detail)
        } catch (error) {
            console.error(error)
            alert('Nepodařilo se načíst data pro zvolený symbol.')
        }
    }

    const handleToggleFavourite = (symbol) => {
        if (favourites.includes(symbol)) {
            setFavourites(favourites.filter(fav => fav !== symbol))
        } else {
            if (favourites.length < 5) {
                setFavourites([...favourites, symbol])
            } else {
                alert('Maximální počet oblíbených akcií je 5!')
            }
        }
    }

    return (
        <div className="app-wrapper">
            <header className="header">
                <div className="logo-area">
                    <img src="/logoMRM.png" alt="Logo" className="logo" />
                </div>
                <div className="search-area">
                    <SearchBar onSelectSymbol={handleSelectSymbol} onShowStock={handleShowStock} />
                </div>
            </header>
            <main className="main-content">
                {selectedStockData && dailyData ? (
                    <StockDetail
                        stockData={selectedStockData}
                        dailyData={dailyData}
                        favourites={favourites}
                        onToggleFavourite={handleToggleFavourite}
                    />
                ) : (
                    <p style={{ marginTop: '2rem' }}>
                        Vyberte symbol a klikněte na "Search" pro zobrazení detailu akcie.
                    </p>
                )}
            </main>
            <aside className="favourites-section">
                <h2>Your favourites</h2>
                <FavouritesTable favourites={favourites} />
            </aside>
            <footer className="footer">
                <p>&copy; Copyright 2025 - {new Date().getFullYear()}</p>
                <a
                    href="https://github.com/matejak47/FM_STIN_BURZA"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    Github link
                </a>
            </footer>
        </div>
    )
}

export default App
