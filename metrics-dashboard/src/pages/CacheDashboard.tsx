import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Sidebar } from '../components/Sidebar/Sidebar.tsx';

const SimpleTable = ({ data }: { data: any[] }) => {
    if (!Array.isArray(data) || data.length === 0)
        return <div className="text-center text-gray-500 py-8">No data to display.</div>;
    const allKeys = Array.from(
        data.reduce((keys, item) => {
            Object.keys(item).forEach((k) => keys.add(k));
            return keys;
        }, new Set<string>()),
    ) as string[];
    return (
        <div className="overflow-x-auto mt-6 rounded-lg shadow border border-gray-200 bg-white">
            <table className="min-w-full divide-y divide-gray-200">
                <thead>
                    <tr>
                        {allKeys.map((key) => (
                            <th
                                key={key}
                                className="px-6 py-3 bg-gray-50 text-left text-xs font-bold text-gray-700 uppercase tracking-wider border-b"
                            >
                                {key}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-100">
                    {data.map((row, idx) => (
                        <tr key={idx} className="hover:bg-red-50 transition-colors">
                            {allKeys.map((key) => (
                                <td key={key} className="px-6 py-3 whitespace-nowrap text-sm text-gray-800 border-b">
                                    {typeof (row as Record<string, any>)[key] === 'object'
                                        ? JSON.stringify((row as Record<string, any>)[key], null, 2)
                                        : String((row as Record<string, any>)[key] ?? '')}
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

const CacheDashboard = () => {
    const [cacheData, setCacheData] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchCacheData = () => {
        setLoading(true);
        axios
            .get('http://localhost:8085/redis/all')
            .then((res) => {
                setCacheData(res.data);
                setLoading(false);
            })
            .catch((err) => {
                setError('Failed to fetch cache data');
                setLoading(false);
            });
    };

    useEffect(() => {
        fetchCacheData();
    }, []);

    const exampleData = [
        { key: 'user:1', value: { name: 'Alice', age: 30, role: 'admin' } },
        { key: 'user:2', value: { name: 'Bob', age: 25, role: 'user' } },
        { key: 'session:xyz', value: 'active' },
        { key: 'metric:cpu', value: 0.73 },
    ];

    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error}</div>;

    return (
        <main className="grid gap-4 p-4 grid-cols-[220px,_1fr]">
            <Sidebar />
            <div className="p-8">
                <h1 className="text-2xl font-bold mb-4">Redis Cache Dashboard</h1>
                <button
                    onClick={fetchCacheData}
                    disabled={loading}
                    className="mb-4 px-4 py-2 bg-red-600 text-white rounded shadow hover:bg-red-700 disabled:opacity-50"
                >
                    {loading ? 'Refreshing...' : 'Refresh'}
                </button>
                <SimpleTable data={cacheData.length === 0 ? exampleData : cacheData} />
            </div>
        </main>
    );
};

export default CacheDashboard;
