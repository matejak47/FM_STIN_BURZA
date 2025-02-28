import {useEffect, useState} from 'react'

function SearchBar({onSelectSymbol, onShowStock}) {
    const [inputValue, setInputValue] = useState('')
    const [allSymbols, setAllSymbols] = useState([]) // pole objektů { symbol, name }
    const [filteredSymbols, setFilteredSymbols] = useState([])

    useEffect(() => {
        const fetchSymbols = async () => {
            try {
                const response = await fetch('/api/burza/all')
                if (!response.ok) {
                    throw new Error('Chyba při načítání symbolů')
                }
                const data = await response.json()
                console.log('Načtené data:', data) // Ověřte, že se vypisuje pole objektů
                setAllSymbols(data)
            } catch (error) {
                console.error(error)
            }
        }
        fetchSymbols()
    }, [])

    const handleInputChange = (e) => {
        const value = e.target.value
        setInputValue(value)

        if (value.length > 0) {
            const filtered = allSymbols.filter((item) =>
                item.symbol.toLowerCase().includes(value.toLowerCase()) ||
                item.name.toLowerCase().includes(value.toLowerCase())
            )
            console.log('Filtrované symboly pro "', value, '":', filtered)
            setFilteredSymbols(filtered)
        } else {
            setFilteredSymbols([])
        }
    }

    const handleSelect = (symbol, name) => {
        console.log('Vybrán symbol:', symbol, 'a název:', name)
        setInputValue(symbol)
        setFilteredSymbols([])
        onSelectSymbol(symbol, name)
    }

    return (
        <div className="search-bar">
            <input
                type="text"
                value={inputValue}
                onChange={handleInputChange}
                placeholder="Search symbol..."
            />
            <button onClick={onShowStock}>Search</button>

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
    )
}

export default SearchBar
