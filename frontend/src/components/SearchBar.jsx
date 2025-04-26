import {useEffect, useState} from 'react';

function SearchBar({onSelectSymbol, onShowStock}) {
    const [inputValue, setInputValue] = useState('');
    const [allSymbols, setAllSymbols] = useState([]);
    const [filteredSymbols, setFilteredSymbols] = useState([]);

    useEffect(() => {
        const fetchSymbols = async () => {
            try {
                const response = await fetch('/api/burza/all');
                if (!response.ok) {
                    throw new Error('Error loading symbols');
                }
                const data = await response.json();
                setAllSymbols(data || []);
            } catch (error) {
                console.error("Error fetching symbols:", error);
                setAllSymbols([]);
            }
        };
        fetchSymbols();
    }, []);

    const handleInputChange = (e) => {
        const value = e.target.value;
        setInputValue(value);

        if (value.length > 0) {
            const filtered = allSymbols.filter((item) =>
                item.symbol.toLowerCase().includes(value.toLowerCase()) ||
                item.name.toLowerCase().includes(value.toLowerCase())
            );
            setFilteredSymbols(filtered);
        } else {
            setFilteredSymbols([]);
        }
    };

    const handleSelect = (symbol, name) => {
        setInputValue(symbol);
        setFilteredSymbols([]);
        onSelectSymbol(symbol, name);
        onShowStock(); // rovnou spustíme hledání po kliknutí na autocomplete
    };

    const handleSearchClick = () => {
        setFilteredSymbols([]); // skryj autocomplete při ručním hledání
        onShowStock();
    };

    return (
        <div className="search-bar">
            <input
                type="text"
                value={inputValue}
                onChange={handleInputChange}
                placeholder="Search symbol..."
            />
            <button onClick={handleSearchClick}>Search</button>

            {filteredSymbols.length > 0 && (
                <ul className="autocomplete-list">
                    {filteredSymbols.map((item) => (
                        <li
                            key={item.symbol}
                            onClick={() => handleSelect(item.symbol, item.name)}
                        >
                            {item.symbol} - {item.name}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default SearchBar;
