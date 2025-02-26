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
                <p style={{ margin: 0 }}>&copy; Copyright 2025 - {new Date().getFullYear()}</p>
                <a
                    href="https://github.com/matejak47/FM_STIN_BURZA"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="24"
                        height="24"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                    >
                        <path d="M12 0C5.37 0 0 5.37 0 12c0 5.3 3.438 9.799 8.207 11.387.6.111.793-.261.793-.578 0-.285-.011-1.04-.016-2.04-3.338.724-4.042-1.61-4.042-1.61-.546-1.385-1.333-1.754-1.333-1.754-1.089-.744.082-.729.082-.729 1.204.085 1.838 1.237 1.838 1.237 1.07 1.834 2.807 1.304 3.492.996.107-.776.42-1.305.763-1.606-2.665-.305-5.467-1.333-5.467-5.931 0-1.31.468-2.381 1.237-3.222-.124-.304-.536-1.526.117-3.18 0 0 1.008-.322 3.3 1.23.96-.267 1.98-.4 3-.404 1.02.004 2.04.137 3 .404 2.292-1.552 3.3-1.23 3.3-1.23.653 1.654.241 2.876.118 3.18.77.841 1.236 1.912 1.236 3.222 0 4.609-2.807 5.624-5.479 5.921.43.372.813 1.103.813 2.222 0 1.606-.015 2.9-.015 3.293 0 .32.192.694.8.576C20.565 21.796 24 17.3 24 12c0-6.63-5.37-12-12-12z"/>
                    </svg>
                </a>
            </footer>
        </div>
    )
}

export default App
