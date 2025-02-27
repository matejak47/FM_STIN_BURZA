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
                    </tr>
                    </thead>
                    <tbody>
                    {favourites.map((symbol) => (
                        <tr key={symbol}>
                            <td onClick={() => {
                                const stock = allStocks.find(s => s.symbol === symbol);
                                const companyName = stock ? stock.name : symbol;
                                onSelectFavourite(symbol, companyName);
                            }}>
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
