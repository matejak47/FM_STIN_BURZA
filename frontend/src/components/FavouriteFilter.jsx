import React, {useState} from 'react';
import "./FavouriteFilter.css"

function FavouriteFilter({onSelectFavourite}) {
    const [days, setDays] = useState('');
    const [filteredFavourites, setFilteredFavourites] = useState([]);

    const handleIncrease = async () => {
        if (!days) return;
        try {
            const response = await fetch(`/api/portfolio/favorites/increase?days=${days}`);
            const data = await response.json();
            setFilteredFavourites(data);
        } catch (error) {
            console.error("Error increasing favourites:", error);
        }
    };

    const handleDecline = async () => {
        if (!days) return;
        try {
            const response = await fetch(`/api/portfolio/favorites/decline?days=${days}`);
            const data = await response.json();
            setFilteredFavourites(data);
        } catch (error) {
            console.error("Error declining favourites:", error);
        }
    };

    return (
        <div className="favourite-filter favourites-section">
            <div className="filter-header">
                <h2>Favourite Filter</h2>
                <div className="filter-controls">
                    <h3>Days: </h3>
                    <input
                        type="number"
                        value={days}
                        onChange={(e) => setDays(e.target.value)}
                        placeholder="Enter days"
                        min="2"
                    />
                    <button className="increase-btn" onClick={handleIncrease}>&#9650;</button>
                    <button className="decline-btn" onClick={handleDecline}>&#9660;</button>
                </div>
            </div>
            {filteredFavourites.length > 0 && (
                <table>
                    <thead>
                    <tr>
                        <th>Filtered Symbols</th>
                    </tr>
                    </thead>
                    <tbody>
                    {filteredFavourites.map((symbol) => (
                        <tr key={symbol}>
                            <td onClick={() => onSelectFavourite(symbol)} style={{cursor: 'pointer'}}>
                                {symbol}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );

}

export default FavouriteFilter;
