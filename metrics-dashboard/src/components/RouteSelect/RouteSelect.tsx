import React, { useState } from 'react';
import { IconType } from 'react-icons/lib';
import { SiPushbullet } from 'react-icons/si';
import { MdMemory } from 'react-icons/md';
import { FaDatabase } from 'react-icons/fa';
import { FaNetworkWired } from 'react-icons/fa6';
import { Link, useNavigate } from 'react-router-dom';

export const RouteSelect = () => {
    const [selectedService, setSelectedService] = useState<string | null>(null);
    const navigate = useNavigate();

    return (
        <div className="space-y-1">
            <Route
                Icon={SiPushbullet}
                selected={selectedService === 'auth-service'}
                title="auth-service"
                onClick={() => setSelectedService(selectedService === 'auth-service' ? null : 'auth-service')}
                showMetrics={selectedService === 'auth-service'}
                selectedService={selectedService}
            />
            <Route
                Icon={SiPushbullet}
                selected={selectedService === 'inventory-service'}
                title="inventory-service"
                onClick={() => setSelectedService(selectedService === 'inventory-service' ? null : 'inventory-service')}
                showMetrics={selectedService === 'inventory-service'}
                selectedService={selectedService}
            />
        </div>
    );
};

const Route = ({
    selected,
    Icon,
    title,
    onClick,
    showMetrics,
    selectedService,
}: {
    selected: boolean;
    Icon: IconType;
    title: string;
    onClick: () => void;
    showMetrics: boolean;
    selectedService: string;
}) => {
    return (
        <div className="space-y-1">
            <button
                onClick={onClick}
                className={`flex items-center justify-start gap-2 w-full rounded px-2 py-1.5 text-sm transition-[box-shadow,_background-color,_color] ${
                    selected
                        ? 'bg-white text-stone-950 shadow'
                        : 'hover:bg-stone-200 bg-transparent text-stone-500 shadow-none'
                }`}
            >
                <Icon size={16} color={selected ? '#8b5cf6' : ''} />
                <span>{title}</span>
            </button>

            {showMetrics && (
                <div className="ml-6 space-y-1">
                    <Link to={`/metrics/${selectedService}/system-metrics`}>
                        <MetricItem icon={MdMemory} label="System Metrics" />
                    </Link>
                    <Link to={`/metrics/${selectedService}/database-metrics`}>
                        <MetricItem icon={FaDatabase} label="Database Metrics" />
                    </Link>
                    <Link to={`/metrics/${selectedService}/network-metrics`}>
                        <MetricItem icon={FaNetworkWired} label="Network Metrics" />
                    </Link>
                </div>
            )}
        </div>
    );
};

const MetricItem = ({ icon: Icon, label }: { icon: IconType; label: string }) => {
    return (
        <button className="flex items-center gap-2 w-full rounded px-2 py-1 text-sm text-stone-500 hover:bg-stone-200">
            <Icon size={14} />
            <span>{label}</span>
        </button>
    );
};
