import React, {useEffect, useState} from 'react';
import StockDetail from './components/StockDetail';
import SearchBar from './components/SearchBar';
import FavouritesTable from './components/FavouritesTable';
import Portfolio from "./components/Portfolio.jsx";

function App() {
    const [selectedSymbol, setSelectedSymbol] = useState('');
    const [selectedName, setSelectedName] = useState('');
    const [dailyData, setDailyData] = useState(null);
    const [selectedStockData, setSelectedStockData] = useState(null);
    const [balance, setBalance] = useState(0);
    const [favourites, setFavourites] = useState([]);
    const [portfolio, setPortfolio] = useState({holdings: {}});
    const [view, setView] = useState('home');

    const handleShowStock = async (symbol) => {
        try {
            const response = await fetch(`/api/burza/daily?symbol=${symbol}`);
            if (!response.ok) throw new Error('Failed to fetch stock data');
            const data = await response.json();

            if (!data || data.length === 0) {
                setSelectedStockData(null);
                setDailyData(null);
                return;
            }

            data.sort((a, b) => new Date(a.date) - new Date(b.date));
            setDailyData(data);

            const lastEntry = data[data.length - 1];
            const stockDetails = {
                symbol,
                open: lastEntry.open,
                close: lastEntry.close,
                high: lastEntry.high,
                low: lastEntry.low,
                date: lastEntry.date,
                volume: lastEntry.volume
            };

            setSelectedStockData(stockDetails);
        } catch (error) {
            console.error('Error fetching stock data:', error);
            alert('Failed to load stock data.');
        }
    };

    // Načtení portfolia a zůstatku z backendu
    const fetchPortfolio = async () => {
        try {
            const response = await fetch('/api/trade/portfolio');
            if (!response.ok) throw new Error('Failed to fetch portfolio');
            const data = await response.json();
            setBalance(data.balance);
            setPortfolio(data);
        } catch (error) {
            console.error(error);
        }
    };

    // Načtení oblíbených akcií
    const fetchFavourites = async () => {
        try {
            const response = await fetch('/api/portfolio/favorites');
            if (!response.ok) throw new Error('Failed to fetch favourites');
            const data = await response.json();
            setFavourites(data);
        } catch (error) {
            console.error(error);
        }
    };

    // Načtení historických dat pro vybranou akcii
    const fetchStockData = async (symbol, name) => {
        try {
            const response = await fetch(`/api/burza/daily?symbol=${symbol}`);
            if (!response.ok) throw new Error('Failed to fetch stock data');
            const data = await response.json();

            // Opravené pořadí grafu (od nejstarších po nejnovější)
            const sortedData = data.sort((a, b) => new Date(a.date) - new Date(b.date));
            setDailyData(sortedData);

            if (sortedData.length > 0) {
                const lastEntry = sortedData[sortedData.length - 1];
                setSelectedStockData({
                    symbol,
                    companyName: name,
                    open: lastEntry.open,
                    close: lastEntry.close,
                    high: lastEntry.high,
                    low: lastEntry.low,
                    date: lastEntry.date,
                    volume: lastEntry.volume
                });
            }
        } catch (error) {
            console.error(error);
        }
    };

    useEffect(() => {
        fetchPortfolio();
        fetchFavourites();
    }, []);

    const handleSelectSymbol = (symbol, name) => {
        setSelectedSymbol(symbol);
        setSelectedName(name);
        fetchStockData(symbol, name);
    };

    const handleBuyStock = async (symbol, name, quantity) => {
        try {
            console.log(`Executing trade for: ${symbol}, Quantity: ${quantity}`);
            const response = await fetch('/api/trade/execute', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    symbol: symbol, // ✅ Ujistíme se, že posíláme pouze symbol (např. "GOOGL")
                    orderType: "BUY",
                    quantity: quantity
                }),
            });

            const result = await response.json();
            console.log("Trade result:", result);
            if (result.success) {
                alert(`Successfully bought ${quantity} shares of ${symbol}`);
                fetchPortfolio(); // ✅ Aktualizace portfolia po nákupu
            } else {
                alert(result.message);
            }
        } catch (error) {
            console.error('Error executing trade:', error);
        }
    };


    const handleSellStock = async (symbol, quantity) => {
        try {
            const response = await fetch('/api/trade/execute', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({symbol, orderType: 'SELL', quantity}),
            });

            const result = await response.json();
            if (result.success) {
                alert(`Successfully sold ${quantity} shares of ${symbol}`);
                fetchPortfolio();
            } else {
                alert(result.message);
            }
        } catch (error) {
            console.error('Error executing trade:', error);
        }
    };

    const handleToggleFavourite = async (symbol) => {
        const isFavourite = favourites.includes(symbol);
        try {
            const response = await fetch(`/api/portfolio/favorites/${symbol}`, {
                method: isFavourite ? 'DELETE' : 'POST',
                headers: {'Content-Type': 'application/json'}
            });

            if (!response.ok) throw new Error('Failed to update favourites');

            // Po změně znovu načteme oblíbené akcie
            await fetchFavourites();
        } catch (error) {
            console.error('Error updating favourites:', error);
        }
    };


    return (
        <div className="app-wrapper">
            <header className="header">
                <div className="logo-area">
                    <img src="/logoMRM.png" alt="Logo" className="logo"/>
                </div>
                <div className="nav-bar">
                    <button className="nav-button" onClick={() => setView(view === 'home' ? 'portfolio' : 'home')}>
                        {view === 'home' ? 'Portfolio' : 'Back'}
                    </button>
                    <div className="balance-container">
                        <span className="balance-text">Balance: ${balance.toFixed(2)}</span>
                    </div>
                    {view === 'home' && (
                        <SearchBar onSelectSymbol={handleSelectSymbol} onShowStock={handleShowStock}/>
                    )}

                </div>
            </header>
            {view === 'home' ? (
                <>
                    <main className="main-content">

                        {selectedStockData && dailyData ? (
                            <StockDetail
                                stockData={selectedStockData}
                                dailyData={dailyData}
                                favourites={favourites}
                                onToggleFavourite={handleToggleFavourite}
                                onBuyStock={handleBuyStock}
                                onSellStock={handleSellStock}
                                balance={balance}
                                portfolio={portfolio}
                            />
                        ) : (
                            <p style={{marginTop: '2rem'}}>
                                Select a stock symbol and click "Search" to view details.
                            </p>
                        )}
                    </main>

                    <aside className="favourites-section">
                        <h2>Favourites</h2>
                        <FavouritesTable
                            favourites={favourites}
                            onToggleFavourite={handleToggleFavourite}
                            portfolio={portfolio}
                            onSelectFavourite={handleShowStock}
                        />
                    </aside>
                </>
            ) : (
                <Portfolio/>
            )}

            <footer className="footer">
                <p style={{margin: 0}}>
                    &copy; {new Date().getFullYear()} - MRM Burza
                </p>
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
                        <path
                            d="M12 0C5.37 0 0 5.37 0 12c0 5.3 3.438 9.799 8.207 11.387.6.111.793-.261.793-.578 0-.285-.011-1.04-.016-2.04-3.338.724-4.042-1.61-4.042-1.61-.546-1.385-1.333-1.754-1.333-1.754-1.089-.744.082-.729.082-.729 1.204.085 1.838 1.237 1.838 1.237 1.07 1.834 2.807 1.304 3.492.996.107-.776.42-1.305.763-1.606-2.665-.305-5.467-1.333-5.467-5.931"
                        />
                    </svg>
                </a>
            </footer>
        </div>
    );
}

export default App;
