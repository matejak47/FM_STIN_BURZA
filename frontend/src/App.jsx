import React, {useEffect, useState} from 'react';
import StockDetail from './components/StockDetail';
import SearchBar from './components/SearchBar';
import FavouritesTable from './components/FavouritesTable';
import Portfolio from "./components/Portfolio.jsx";
import FavouriteFilter from "./components/FavouriteFilter.jsx";
import Log from './components/Log';
import './App.css'


function App() {
    const [selectedSymbol, setSelectedSymbol] = useState('');
    const [selectedName, setSelectedName] = useState('');
    const [dailyData, setDailyData] = useState(null);
    const [selectedStockData, setSelectedStockData] = useState(null);
    const [balance, setBalance] = useState(0);
    const [favourites, setFavourites] = useState([]);
    const [portfolio, setPortfolio] = useState({holdings: {}});
    const [view, setView] = useState('home');
    const [allStocks, setAllStocks] = useState([]);

    useEffect(() => {
        fetch("/api/burza/all")
            .then(response => response.json())
            .then(data => {
                setAllStocks(data);
                console.log("Loaded stock symbols and names:", data);
            })
            .catch(error => console.error("Error fetching stock list:", error));
    }, []);


    const handleShowStock = async (symbol, name = '') => {
        try {
            console.log("Fetching stock for:", symbol, "with name:", name);  // Debug log

            const response = await fetch(`/api/burza/daily?symbol=${symbol}`);
            if (!response.ok) throw new Error('Failed to fetch stock data');
            const data = await response.json();

            if (!data || data.length === 0) {
                setSelectedStockData(null);
                setDailyData([]);
                return;
            }

            data.sort((a, b) => new Date(a.date) - new Date(b.date));

            const lastEntry = data[data.length - 1];
            const stockDetails = {
                symbol,
                companyName: name || "Unknown Company",
                open: lastEntry.open,
                close: lastEntry.close,
                high: lastEntry.high,
                low: lastEntry.low,
                date: lastEntry.date,
                volume: lastEntry.volume
            };

            console.log("Stock details being set:", stockDetails);
            setSelectedStockData(stockDetails);
            setDailyData(data);
        } catch (error) {
            console.error('Error fetching stock data:', error);
            alert('Failed to load stock data.');
        }
    };

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

    const fetchStockData = async (symbol, name) => {
        try {
            const response = await fetch(`/api/burza/daily?symbol=${symbol}`);
            if (!response.ok) throw new Error('Failed to fetch stock data');
            const data = await response.json();

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
                    symbol: symbol,
                    orderType: "BUY",
                    quantity: quantity
                }),
            });

            const result = await response.json();
            console.log("Trade result:", result);
            if (result.success) {
                await fetchPortfolio();
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
                await fetchPortfolio();
            } else {
                alert(result.message);
            }
        } catch (error) {
            console.error('Error executing trade:', error);
        }
    };

    const handleToggleFavourite = async (symbol, name) => {
        const isFavourite = favourites.some(fav => fav.symbol === symbol);

        try {
            if (isFavourite) {
                await fetch(`/api/portfolio/favorites/${symbol}`, {
                    method: 'DELETE',
                    headers: {'Content-Type': 'application/json'}
                });
            } else {
                await fetch(`/api/portfolio/favorites`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({symbol, name})
                });
            }
            await fetchFavourites();
        } catch (error) {
            console.error('Error updating favourites:', error);
        }
    };
    const handleTransactionClick = async () => {
        try {
            const response = await fetch('/api/rating', {
                method: 'POST'
            });
            if (!response.ok) {
                throw new Error('Failed to trigger transaction');
            }
            alert('Transaction triggered successfully!');
        } catch (error) {
            console.error('Error triggering transaction:', error);
            alert('Failed to trigger transaction.');
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
                        <div className="tlacitko">
                            <h2>Favourites</h2>
                            <button onClick={handleTransactionClick}>Trigger Transaction</button>
                        </div>

                        <FavouritesTable
                            favourites={favourites}
                            onToggleFavourite={handleToggleFavourite}
                            portfolio={portfolio}
                            onSelectFavourite={handleShowStock}
                            allStocks={allStocks}
                        />
                    </aside>
                    <FavouriteFilter onSelectFavourite={handleShowStock} allStocks={allStocks}/>
                    <Log/>
                </>
            ) : (
                <Portfolio setBalance={setBalance}/>

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
                            d="M12.006 2a9.847 9.847 0 0 0-6.484 2.44 10.32 10.32 0 0 0-3.393 6.17 10.48 10.48 0 0 0 1.317 6.955 10.045 10.045 0 0 0 5.4 4.418c.504.095.683-.223.683-.494 0-.245-.01-1.052-.014-1.908-2.78.62-3.366-1.21-3.366-1.21a2.711 2.711 0 0 0-1.11-1.5c-.907-.637.07-.621.07-.621.317.044.62.163.885.346.266.183.487.426.647.71.135.253.318.476.538.655a2.079 2.079 0 0 0 2.37.196c.045-.52.27-1.006.635-1.37-2.219-.259-4.554-1.138-4.554-5.07a4.022 4.022 0 0 1 1.031-2.75 3.77 3.77 0 0 1 .096-2.713s.839-.275 2.749 1.05a9.26 9.26 0 0 1 5.004 0c1.906-1.325 2.74-1.05 2.74-1.05.37.858.406 1.828.101 2.713a4.017 4.017 0 0 1 1.029 2.75c0 3.939-2.339 4.805-4.564 5.058a2.471 2.471 0 0 1 .679 1.897c0 1.372-.012 2.477-.012 2.814 0 .272.18.592.687.492a10.05 10.05 0 0 0 5.388-4.421 10.473 10.473 0 0 0 1.313-6.948 10.32 10.32 0 0 0-3.39-6.165A9.847 9.847 0 0 0 12.007 2Z"></path>
                        />
                    </svg>
                </a>
            </footer>
        </div>
    );
}

export default App;
