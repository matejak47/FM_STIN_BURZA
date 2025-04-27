import React, {useEffect, useState} from 'react';
import "./FavouriteFilter.css";

function FavouriteFilter({onSelectFavourite}) {
    const [filteredFavourites, setFilteredFavourites] = useState([]);

    useEffect(() => {
        // Zavoláme API hned při načtení komponenty
        async function fetchFilteredFavourites() {
            try {
                const response = await fetch("/api/rating");
                const data = await response.json();
                setFilteredFavourites(data); // uložíme do stavu
            } catch (error) {
                console.error("Chyba při načítání filtrovaných akcií:", error);
            }
        }

        fetchFilteredFavourites();
    }, []);

    return (
        <div className="favourite-filter favourites-section">
            <div className="filter-header">
                <h2>Favourite Filter</h2>
            </div>

            {filteredFavourites.length > 0 ? (
                <table>
                    <thead>
                    <tr>
                        <th>Company</th>
                        <th>Symbol</th>
                        {/* pokud chceš, můžeme tady později přidat Date, Rating, Sale */}
                    </tr>
                    </thead>
                    <tbody>
                    {filteredFavourites.map((stock) => (
                        <tr key={stock.symbol}>
                            <td
                                onClick={() => onSelectFavourite(stock.symbol, stock.name)}
                                className="clickable"
                            >
                                {stock.name}
                            </td>
                            <td>{stock.symbol}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            ) : (
                <p>Žádné akcie neprošly filtrem.</p> // fallback pokud prázdné
            )}
        </div>
    );
}

export default FavouriteFilter;
