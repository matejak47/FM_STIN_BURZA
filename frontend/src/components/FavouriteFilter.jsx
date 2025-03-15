import React, {useState} from 'react';
import "./FavouriteFilter.css";

function FavouriteFilter({onSelectFavourite, allStocks}) {
    const [days, setDays] = useState('');
    const [filteredFavourites, setFilteredFavourites] = useState([]);

    const sendToBackend = async (data) => {
        if (!data.length) {
            console.warn("No data to send.");
            return;
        }

        try {
            const response = await fetch(`/api/portfolio/send-to-external`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error(`Failed to send data. Status: ${response.status}`);
            }

            const result = await response.json();
            console.log("Data successfully sent to backend:", result);
        } catch (error) {
            console.error("Error sending data to backend:", error);
        }
    };

    const handleIncrease = async () => {
        if (!days) return;
        try {
            const response = await fetch(`/api/portfolio/favorites/increase?days=${days}`);
            const data = await response.json();
            setFilteredFavourites(data);

            // Send data to backend after fetching
            await sendToBackend(data);
        } catch (error) {
            console.error("Error fetching increasing favourites:", error);
        }
    };

    const handleDecline = async () => {
        if (!days) return;
        try {
            const response = await fetch(`/api/portfolio/favorites/decline?days=${days}`);
            const data = await response.json();
            setFilteredFavourites(data);

            // Send data to backend after fetching
            await sendToBackend(data);
        } catch (error) {
            console.error("Error fetching declining favourites:", error);
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
