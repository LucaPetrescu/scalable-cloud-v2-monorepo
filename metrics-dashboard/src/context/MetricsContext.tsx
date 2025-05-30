import React, { createContext, useContext, useState, useEffect } from 'react';
import { useSystemMetrics } from '../hooks/metrics/useSystemMetrics.tsx';
import { useNetworkMetrics } from '../hooks/metrics/useNetworkMetrics.tsx';
import { useDatabaseMetrics } from '../hooks/metrics/useDatabaseMetrics.tsx';

const MetricsContext = createContext({});

export const MetricsProvider = ({ children }) => {
    const systemMetrics = useSystemMetrics();
    const networkMetrics = useNetworkMetrics();
    const databaseMetrics = useDatabaseMetrics();

    return (
        <MetricsContext.Provider value={{ systemMetrics, networkMetrics, databaseMetrics }}>
            {children}
        </MetricsContext.Provider>
    );
};

export const useMetrics = () => useContext(MetricsContext);
