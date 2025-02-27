import React, {useEffect} from 'react';

function FavouritesTable({favourites, portfolio, onSelectFavourite}) {
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
                    </tr>
                    </thead>
                    <tbody>
                    {favourites.map((symbol) => (
                        <tr key={symbol}>
                            <td onClick={() => onSelectFavourite(symbol, portfolio.holdings[symbol]?.name || symbol)}>
                                {symbol}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default FavouritesTable;
