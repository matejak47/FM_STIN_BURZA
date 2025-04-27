import React from 'react';
import "./FavouriteFilter.css";

function FavouriteFilter({onSelectFavourite, filteredFavourites = []}) {
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
                <p>Žádné akcie neprošly filtrem.</p>
            )}
        </div>
    );
}

export default FavouriteFilter;
