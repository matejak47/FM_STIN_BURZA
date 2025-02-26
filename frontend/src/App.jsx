import { useState } from 'react'
import SearchBar from './components/SearchBar'
import StockDetail from './components/StockDetail'
import FavouritesTable from './components/FavouritesTable'

function App() {
    const [selectedSymbol, setSelectedSymbol] = useState('')
    const [selectedName, setSelectedName] = useState('')

    const [selectedStockData, setSelectedStockData] = useState(null)

    const [favourites, setFavourites] = useState([])

    /**
     * Uloží vybraný symbol a jméno akcie (z SearchBaru).
     */
    const handleSelectSymbol = (symbol, name) => {
        setSelectedSymbol(symbol)
        setSelectedName(name)
    }

    /**
     * Po kliknutí na "Search" stáhneme data z /api/burza/daily?symbol=...
     */
    const handleShowStock = async () => {
        if (!selectedSymbol) return

        try {
            const response = await fetch(`/api/burza/daily?symbol=${selectedSymbol}`)
            if (!response.ok) {
                throw new Error('Chyba při načítání daily data')
            }
            const dailyData = await response.json()

            if (!dailyData || dailyData.length === 0) {
                setSelectedStockData(null)
                return
            }

            const todayData = dailyData[0]
            const detail = {
                symbol: selectedSymbol,
                companyName: selectedName,
                open: todayData.open,
                close: todayData.close,
                high: todayData.high,
                low: todayData.low,
                date: todayData.date,
            }
            setSelectedStockData(detail)
        } catch (error) {
            console.error(error)
            alert('Nepodařilo se načíst data pro zvolený symbol.')
        }
    }

    /**
     * Přidá/odebere symbol z oblíbených (max 5).
     */
    const handleToggleFavourite = (symbol) => {
        if (favourites.includes(symbol)) {
            // Odeber
            setFavourites(favourites.filter((fav) => fav !== symbol))
        } else {
            // Přidej, pokud není překročen limit
            if (favourites.length < 5) {
                setFavourites([...favourites, symbol])
            } else {
                alert('Maximální počet oblíbených akcií je 5!')
            }
        }
    }

    return (
        <div className="app-wrapper">
            {/* Hlavička */}
            <header className="header">
                <div className="logo-area">
                    {/* Cesta k vašemu logu */}
                    <img src="/logoMRM.png" alt="Logo" className="logo" />

                </div>
                <div className="search-area">
                    <SearchBar
                        onSelectSymbol={handleSelectSymbol}
                        onShowStock={handleShowStock}
                    />
                </div>
            </header>

            {/* Hlavní obsah */}
            <main className="main-content">
                {selectedStockData ? (
                    <StockDetail
                        stockData={selectedStockData}
                        favourites={favourites}
                        onToggleFavourite={handleToggleFavourite}
                    />
                ) : (
                    <p style={{ marginTop: '2rem' }}>
                        Vyberte symbol a klikněte na "Search" pro zobrazení detailu akcie.
                    </p>
                )}
            </main>

            {/* Oblíbené akcie */}
            <aside className="favourites-section">
                <h2>Your favourites</h2>
                <FavouritesTable favourites={favourites} />
            </aside>

            {/* Patička */}
            <footer className="footer">
                <p>&copy;Copyright 2025 - {new Date().getFullYear()}</p>
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
