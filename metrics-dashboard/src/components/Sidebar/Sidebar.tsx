import React from 'react';
import { Logo } from '../Logo/Logo.tsx';
import { Search } from '../Search/Search.tsx';
import { RouteSelect } from '../RouteSelect/RouteSelect.tsx';
import { Link } from 'react-router-dom';
import { MdCached } from 'react-icons/md';

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
                    className="flex items-center gap-1 mt-2 px-1.5 py-1 rounded-md bg-gradient-to-r from-blue-500 to-blue-700 text-white font-bold shadow border border-blue-600 hover:from-blue-600 hover:to-blue-800 transition-colors duration-200 text-sm w-32"
                >
                    <span style={{ display: 'flex', alignItems: 'center' }}>
                        <MdCached />
                    </span>
                    Cache Dashboard
                </Link>
            </div>
        </div>
    );
};
