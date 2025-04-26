import React, {useEffect} from 'react';

function FavouritesTable({favourites, portfolio, onSelectFavourite, allStocks}) {
    useEffect(() => {
        console.log("Favourites:", favourites);
        console.log("Portfolio:", portfolio);
    }, [favourites, portfolio]);

    if (!portfolio) {
        return <p>Loading portfolio...</p>;
    }

    return (
        <div className="favourites-table">
            {favourites.length === 0 ? (
                <p>No favourites yet.</p>
            ) : (
                <table>
                    <thead>
                    <tr>
                        <th>Symbol</th>
                        <th>Company Name</th>
                    </tr>
                    </thead>
                    <tbody>
                    {favourites.map((fav) => (
                        <tr key={fav.symbol}>
                            <td
                                onClick={() => onSelectFavourite(fav.symbol, fav.name)}
                                className="clickable"
                            >
                                {fav.symbol}
                            </td>
                            <td>{fav.name}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default FavouritesTable;
