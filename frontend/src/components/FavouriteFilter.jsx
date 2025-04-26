import React, {useState} from 'react';
import "./FavouriteFilter.css";

function FavouriteFilter({onSelectFavourite, allStocks}) {
    const [filteredFavourites, setFilteredFavourites] = useState([]);

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
                                className="clickable">
                                {stock.name}
                            </td>
                            <td>{new Date(stock.date).toLocaleDateString()}</td>
                            <td>{stock.rating}</td>
                            <td>{stock.sale !== null ? stock.sale : "N/A"}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default FavouriteFilter;
