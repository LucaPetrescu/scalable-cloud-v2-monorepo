import React from 'react';
import { useParams } from 'react-router-dom';
import { Sidebar } from '../components/Sidebar/Sidebar.tsx';
import { Topbar } from '../components/Topbar/Topbar.tsx';
import { Grid } from '../components/Grid/Grid.tsx';
const Metrics = () => {
    const { service, metricType } = useParams();

    return (
        <main className="grid gap-4 p-4 grid-cols-[220px,_1fr]">
            <Sidebar />
            <div className="flex flex-col gap-4">
                <h1 className="text-2xl font-bold">
                    {service} {'>'} {metricType}
                </h1>
                <div className="bg-white rounded-lg shadow p-6 min-h-[calc(100vh-2rem)] flex flex-col gap-4">
                    <Topbar />
                    <Grid />
                </div>
            </div>
        </main>
    );
};

export default Metrics;
