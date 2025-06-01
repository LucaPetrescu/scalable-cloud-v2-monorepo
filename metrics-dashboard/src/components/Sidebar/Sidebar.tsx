import React from 'react';
import { Logo } from '../Logo/Logo.tsx';
import { Search } from '../Search/Search.tsx';
import { RouteSelect } from '../RouteSelect/RouteSelect.tsx';
import { Link } from 'react-router-dom';
import { DiRedis } from 'react-icons/di';

export const Sidebar = () => {
    return (
        <div>
            <div className="overflow-y-scroll sticky top-4 h-[calc(100vh-32px-48px)]">
                <Link to="/">
                    <Logo />
                </Link>
                <Search />
                <RouteSelect />
                <Link
                    to="/cache-dashboard"
                    className="flex items-center gap-1 mt-2 px-1.5 py-1 rounded-md bg-gradient-to-r from-red-500 to-red-700 text-white font-bold shadow border border-red-600 hover:from-red-600 hover:to-red-800 transition-colors duration-200 text-sm"
                >
                    <span style={{ display: 'flex', alignItems: 'center' }}>
                        <DiRedis size={16} color="#fff" />
                    </span>
                    Redis Cache Dashboard
                </Link>
            </div>
        </div>
    );
};
