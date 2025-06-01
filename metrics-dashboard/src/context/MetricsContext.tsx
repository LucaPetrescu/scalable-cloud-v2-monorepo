import React, { createContext, useContext, useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useSystemMetrics } from '../hooks/metrics/useSystemMetrics.tsx';
import { useNetworkMetrics } from '../hooks/metrics/useNetworkMetrics.tsx';
import { useDatabaseMetrics } from '../hooks/metrics/useDatabaseMetrics.tsx';

const MetricsContext = createContext({});

export const MetricsProvider = ({ children }) => {
    const { service } = useParams();
    const systemMetrics = useSystemMetrics(service);
    const networkMetrics = useNetworkMetrics(service);
    const databaseMetrics = useDatabaseMetrics(service);

    return (
        <MetricsContext.Provider value={{ systemMetrics, networkMetrics, databaseMetrics }}>
            {children}
        </MetricsContext.Provider>
    );
};

export const useMetrics = () => useContext(MetricsContext);
