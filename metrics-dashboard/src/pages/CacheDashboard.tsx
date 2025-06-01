import React, { useEffect, useState } from 'react';
import axios from 'axios';

const CacheDashboard = () => {
    const [cacheData, setCacheData] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        axios
            .get('http://localhost:8085/cache/all')
            .then((res) => {
                setCacheData(res.data);
                setLoading(false);
            })
            .catch((err) => {
                setError('Failed to fetch cache data');
                setLoading(false);
            });
    }, []);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error}</div>;

    if (!Array.isArray(cacheData) || cacheData.length === 0) {
        return <div>No cached data found.</div>;
    }

    // Get all unique keys for table columns
    const allKeys = Array.from(
        cacheData.reduce((keys, item) => {
            Object.keys(item).forEach((k) => keys.add(k));
            return keys;
        }, new Set<string>()),
    );

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold mb-4">Redis Cache Dashboard</h1>
            <div className="overflow-x-auto">
                <table className="min-w-full border border-gray-300">
                    <thead>
                        <tr>
                            {allKeys.map((key) => (
                                <th key={key} className="border px-4 py-2 bg-gray-100">
                                    {key}
                                </th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {cacheData.map((row, idx) => (
                            <tr key={idx}>
                                {allKeys.map((key) => (
                                    <td key={key} className="border px-4 py-2">
                                        {String(row[key] ?? '')}
                                    </td>
                                ))}
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default CacheDashboard;
