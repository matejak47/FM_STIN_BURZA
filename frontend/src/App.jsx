import React, {useEffect, useState} from 'react';
import {fetchStockData} from './utils/fetchStockData';
import StockDetail from './components/StockDetail';
import SearchBar from './components/SearchBar';
import FavouritesTable from './components/FavouritesTable';
import Portfolio from "./components/Portfolio";

function App() {
    const [selectedSymbol, setSelectedSymbol] = useState('');
    const [selectedName, setSelectedName] = useState('');
    const [dailyData, setDailyData] = useState(null);
    const [selectedStockData, setSelectedStockData] = useState(null);
    const [view, setView] = useState('home');

    // Načtení dat z localStorage
    const [balance, setBalance] = useState(() => parseFloat(localStorage.getItem('balance')) || 50000.0);
    const [favourites, setFavourites] = useState(() => {
        const savedFavourites = localStorage.getItem('favourites');
        return savedFavourites ? JSON.parse(savedFavourites) : [];
    });
    const [portfolio, setPortfolio] = useState(() => {
        const savedPortfolio = localStorage.getItem('portfolio');
        return savedPortfolio ? JSON.parse(savedPortfolio) : {};
    });

    // Automatické ukládání do localStorage při změně dat
    useEffect(() => localStorage.setItem('favourites', JSON.stringify(favourites)), [favourites]);
    useEffect(() => localStorage.setItem('balance', balance.toString()), [balance]);
    useEffect(() => localStorage.setItem('portfolio', JSON.stringify(portfolio)), [portfolio]);

    const handleShowStock = async (symbolParam, nameParam) => {
        const sym = symbolParam || selectedSymbol;
        const compName = nameParam || selectedName;
        if (!sym) return;

        try {
            const allData = await fetchStockData(sym);
            if (!allData || allData.length === 0) {
                setSelectedStockData(null);
                setDailyData(null);
                return;
            }

            allData.sort((a, b) => new Date(a.date) - new Date(b.date));
            setDailyData(allData);

            const lastEntry = allData[allData.length - 1];
            setSelectedStockData({
                symbol: sym,
                companyName: compName,
                open: lastEntry.open,
                close: lastEntry.close,
                high: lastEntry.high,
                low: lastEntry.low,
                date: lastEntry.date,
                volume: lastEntry.volume
            });
        } catch (error) {
            console.error(error);
            alert('Failed to load stock data.');
        }
    };

    const handleSelectSymbol = (symbol, name) => {
        setSelectedSymbol(symbol);
        setSelectedName(name);
    };

    const handleToggleFavourite = (symbol, name) => {
        setFavourites(prev => {
            const existingStock = prev.find(fav => fav.symbol === symbol);
            if (existingStock) {
                localStorage.setItem(`fav_${symbol}`, JSON.stringify(existingStock));
                return prev.filter(fav => fav.symbol !== symbol);
            } else {
                const previousData = portfolio[symbol] || {quantity: 0, totalValue: 0};
                return prev.length < 5
                    ? [...prev, {symbol, name, quantity: previousData.quantity, totalValue: previousData.totalValue}]
                    : (alert('Maximum 5 favourites allowed!'), prev);
            }
        });
    };

    const handleBuyStock = (symbol, name, quantity, price) => {
        const totalCost = quantity * price;
        if (totalCost > balance) {
            alert('Not enough funds!');
            return;
        }

        setBalance(prevBalance => prevBalance - totalCost);
        setPortfolio(prev => {
            const existingStock = prev[symbol] || {quantity: 0, totalValue: 0};
            return {
                ...prev,
                [symbol]: {
                    quantity: existingStock.quantity + quantity,
                    totalValue: (existingStock.quantity + quantity) * price
                }
            };
        });

        setFavourites(prev =>
            prev.map(fav =>
                fav.symbol === symbol
                    ? {...fav, quantity: fav.quantity + quantity, totalValue: (fav.quantity + quantity) * price}
                    : fav
            )
        );
    };

    return (
        <div className="app-wrapper">
            <header className="header">
                <div className="logo-area">
                    <img
                        src="/logoMRM.png"
                        alt="Logo"
                        className="logo"
                        onClick={() => setView('home')}
                        style={{cursor: 'pointer'}}
                    />
                </div>
                <div className="nav-bar">
                    <button className="nav-button" onClick={() => setView(view === 'home' ? 'portfolio' : 'home')}>
                        {view === 'home' ? 'Portfolio' : 'Back'}
                    </button>
                    {view === 'home' && (
                        <SearchBar onSelectSymbol={handleSelectSymbol} onShowStock={handleShowStock}/>
                    )}
                </div>
            </header>

            <main className="main-content">
                {view === 'home' ? (
                    <>
                        {selectedStockData && dailyData ? (
                            <StockDetail
                                stockData={selectedStockData}
                                dailyData={dailyData}
                                favourites={favourites}
                                onToggleFavourite={handleToggleFavourite}
                                onBuyStock={handleBuyStock}
                                balance={balance}
                            />
                        ) : (
                            <p style={{marginTop: '2rem'}}>
                                Select a stock symbol and click "Search" to view details.
                            </p>
                        )}
                    </>
                ) : (
                    <Portfolio/>
                )}
            </main>

            {/* Skrytí Favourites v Portfoliu */}
            {view === 'home' && (
                <aside className="favourites-section">
                    <h2>Favourites</h2>
                    <FavouritesTable
                        favourites={favourites}
                        onToggleFavourite={handleToggleFavourite}
                        onSelectFavourite={handleShowStock}
                    />
                </aside>
            )}

            <footer className="footer">
                <p style={{margin: 0}}>
                    &copy;2025 - {new Date().getFullYear()} - MRM Burza
                </p>
                <a href="https://github.com/matejak47/FM_STIN_BURZA" target="_blank" rel="noopener noreferrer">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                         fill="currentColor">
                        <path
                            d="M12 0C5.37 0 0 5.37 0 12c0 5.3 3.438 9.799 8.207 11.387.6.111.793-.261.793-.578 0-.285-.011-1.04-.016-2.04-3.338.724-4.042-1.61-4.042-1.61-.546-1.385-1.333-1.754-1.333-1.754"/>
                    </svg>
                </a>
            </footer>
        </div>
    );
}

export default App;
