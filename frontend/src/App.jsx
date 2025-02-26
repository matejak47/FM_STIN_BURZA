import React, {useEffect, useState} from 'react';
import {fetchStockData} from './utils/fetchStockData';
import StockDetail from './components/StockDetail';
import SearchBar from './components/SearchBar';
import FavouritesTable from './components/FavouritesTable';

function App() {
    const [selectedSymbol, setSelectedSymbol] = useState('');
    const [selectedName, setSelectedName] = useState('');
    const [dailyData, setDailyData] = useState(null);
    const [selectedStockData, setSelectedStockData] = useState(null);

    // Načtení balance z localStorage
    const [balance, setBalance] = useState(() => {
        return parseFloat(localStorage.getItem('balance')) || 50000.0;
    });

    // Načtení favourites z localStorage
    const [favourites, setFavourites] = useState(() => {
        const savedFavourites = localStorage.getItem('favourites');
        return savedFavourites ? JSON.parse(savedFavourites) : [];
    });

    // Načtení portfolia z localStorage
    const [portfolio, setPortfolio] = useState(() => {
        const savedPortfolio = localStorage.getItem('portfolio');
        return savedPortfolio ? JSON.parse(savedPortfolio) : {};
    });

    // Automatické ukládání do localStorage při změně dat
    useEffect(() => {
        localStorage.setItem('favourites', JSON.stringify(favourites));
    }, [favourites]);

    useEffect(() => {
        localStorage.setItem('balance', balance.toString());
    }, [balance]);

    useEffect(() => {
        localStorage.setItem('portfolio', JSON.stringify(portfolio));
    }, [portfolio]);

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
            const detail = {
                symbol: sym,
                companyName: compName,
                open: lastEntry.open,
                close: lastEntry.close,
                high: lastEntry.high,
                low: lastEntry.low,
                date: lastEntry.date,
                volume: lastEntry.volume
            };
            setSelectedStockData(detail);
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
            const isAlreadyFavourite = existingStock !== undefined;

            if (isAlreadyFavourite) {
                localStorage.setItem(`fav_${symbol}`, JSON.stringify(existingStock));
                return prev.filter(fav => fav.symbol !== symbol);
            } else {
                // Načtení dat z portfolia, pokud už byla akcie koupena
                const previousData = portfolio[symbol] || {quantity: 0, totalValue: 0};

                if (prev.length < 5) {
                    return [...prev, {
                        symbol,
                        name,
                        quantity: previousData.quantity,
                        totalValue: previousData.totalValue
                    }];
                } else {
                    alert('Maximum 5 favourites allowed!');
                    return prev;
                }
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

        // Aktualizace portfolia
        setPortfolio(prev => {
            const existingStock = prev[symbol] || {quantity: 0, totalValue: 0};
            const updatedStock = {
                quantity: existingStock.quantity + quantity,
                totalValue: (existingStock.quantity + quantity) * price
            };
            return {...prev, [symbol]: updatedStock};
        });

        // Aktualizace favourites
        setFavourites(prev => {
            return prev.map(fav => {
                if (fav.symbol === symbol) {
                    return {
                        ...fav,
                        quantity: fav.quantity + quantity,
                        totalValue: (fav.quantity + quantity) * price
                    };
                }
                return fav;
            });
        });
    };

    return (
        <div className="app-wrapper">
            <header className="header">
                <div className="logo-area">
                    <img src="/logoMRM.png" alt="Logo" className="logo"/>
                </div>
                <div className="search-area">
                    <div className="balance-container">
                        <span className="balance-text">Balance: ${balance.toFixed(2)}</span>
                    </div>
                    <SearchBar onSelectSymbol={handleSelectSymbol} onShowStock={() => handleShowStock()}/>

                </div>
            </header>

            <main className="main-content">
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
            </main>

            <aside className="favourites-section">
                <h2>Favourites</h2>
                <FavouritesTable
                    favourites={favourites}
                    onToggleFavourite={handleToggleFavourite}
                    onSelectFavourite={handleShowStock}
                />
            </aside>
            <footer className="footer">
                <p style={{margin: 0}}>
                    &copy;2025 - {new Date().getFullYear()} - MRM Burza
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
                            d="M12 0C5.37 0 0 5.37 0 12c0 5.3 3.438 9.799 8.207 11.387.6.111.793-.261.793-.578 0-.285-.011-1.04-.016-2.04-3.338.724-4.042-1.61-4.042-1.61-.546-1.385-1.333-1.754-1.333-1.754-1.089-.744.082-.729.082-.729 1.204.085 1.838 1.237 1.838 1.237 1.07 1.834 2.807 1.304 3.492.996.107-.776.42-1.305.763-1.606-2.665-.305-5.467-1.333-5.467-5.931 0-1.31.468-2.381 1.237-3.222-.124-.304-.536-1.526.117-3.18 0 0 1.008-.322 3.3 1.23.96-.267 1.98-.4 3-.404 1.02.004 2.04.137 3 .404 2.292-1.552 3.3-1.23 3.3-1.23.653 1.654.241 2.876.118 3.18.77.841 1.236 1.912 1.236 3.222 0 4.609-2.807 5.624-5.479 5.921.43.372.813 1.103.813 2.222 0 1.606-.015 2.9-.015 3.293 0 .32.192.694.8.576C20.565 21.796 24 17.3 24 12c0-6.63-5.37-12-12-12z"
                        />
                    </svg>
                </a>
            </footer>
        </div>
    );
}

export default App;
