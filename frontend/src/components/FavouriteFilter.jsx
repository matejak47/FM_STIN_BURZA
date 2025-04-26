import React from 'react';
import "./FavouriteFilter.css";

function FavouriteFilter({onSelectFavourite, filteredFavourites = []}) {
    return (
        <div className="favourite-filter favourites-section">
            <div className="filter-header">
                <h2>Favourite Filter</h2>
            </div>

            {filteredFavourites.length > 0 && (
                <table>
                    <thead>
                    <tr>
                        <th>Company</th>
                        <th>Date</th>
                        <th>Rating</th>
                        <th>Sale</th>
                    </tr>
                    </thead>
                    <tbody>
                    {filteredFavourites.map((stock) => (
                        <tr key={stock.name}>
                            <td
                                onClick={() => onSelectFavourite(stock.name, stock.name)}
                                className="clickable"
                            >
                                {stock.name}
                            </td>
                            <td>{stock.date ? new Date(stock.date).toLocaleDateString() : "N/A"}</td>
                            <td>{stock.rating ?? "N/A"}</td>
                            <td>{stock.sale !== null && stock.sale !== undefined ? stock.sale : "N/A"}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default FavouriteFilter;
