import React from "react";
import { Command } from "cmdk";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { MdMemory } from "react-icons/md";
import { FaDatabase } from "react-icons/fa";
import { FaNetworkWired } from "react-icons/fa6";


export const ServiceMenu = ({
    open,
    setOpen,
}: {
    open: boolean;
    setOpen: Dispatch<SetStateAction<boolean>>;
}) => {
    const [value, setValue] = useState("");

    useEffect(() => {
        const down = (e: KeyboardEvent) => {
            if (e.key === "k" && (e.metaKey || e.ctrlKey)) {
                e.preventDefault();
                setOpen((open) => !open);
            }
        };

        document.addEventListener("keydown", down);
        return () => document.removeEventListener("keydown", down);
    }, []);

    const shouldShowGroup = (groupName: string) => {
        if (!value) return true;
        return groupName.toLowerCase().includes(value.toLowerCase());
    };

    return (
        <Command.Dialog
            open={open}
            onOpenChange={setOpen}
            label="Global Command Menu"
            className="fixed inset-0 bg-stone-950/50"
            onClick={() => setOpen(false)}
        >
            <div
                onClick={(e) => e.stopPropagation()}
                className="bg-white rounded-lg shadow-xl border-stone-300 border overflow-hidden w-full max-w-lg mx-auto mt-12"
            >
                <Command.Input
                    value={value}
                    onValueChange={setValue}
                    placeholder="What do you need?"
                    className="relative border-b border-stone-300 p-3 text-lg w-full placeholder:text-stone-400 focus:outline-none"
                />
                <Command.List className="p-3">
                    <Command.Empty>
                        No results found for{" "}
                        <span className="text-violet-500">"{value}"</span>
                    </Command.Empty>


                    <Command.Group heading="Auth Service" className="text-sm mb-3 text-stone-700">
                        <Command.Item value="auth service system metrics" className="flex cursor-pointer transition-colors p-2 text-sm text-stone-950 hover:bg-stone-200 rounded items-center gap-2">
                            <MdMemory />
                            System Metrics
                        </Command.Item>
                        <Command.Item value="auth service database metrics" className="flex cursor-pointer transition-colors p-2 text-sm text-stone-950 hover:bg-stone-200 rounded items-center gap-2">
                            <FaDatabase />
                            Database Metrics
                        </Command.Item>
                        <Command.Item value="auth service network metrics" className="flex cursor-pointer transition-colors p-2 text-sm text-stone-950 hover:bg-stone-200 rounded items-center gap-2">
                            <FaNetworkWired />
                            Network Metrics
                        </Command.Item>
                    </Command.Group>

                    <Command.Group
                        heading="Inventory Service"
                        className="text-sm text-stone-700 mb-3"
                    >
                        <Command.Item value="inventory service system metrics" className="flex cursor-pointer transition-colors p-2 text-sm text-stone-950 hover:bg-stone-200 rounded items-center gap-2">
                            <MdMemory />
                            System Metrics
                        </Command.Item>
                        <Command.Item value="inventory service database metrics" className="flex cursor-pointer transition-colors p-2 text-sm text-stone-950 hover:bg-stone-200 rounded items-center gap-2">
                            <FaDatabase />
                            Database Metrics
                        </Command.Item>
                        <Command.Item value="inventory service network metrics" className="flex cursor-pointer transition-colors p-2 text-sm text-stone-950 hover:bg-stone-200 rounded items-center gap-2">
                            <FaNetworkWired />
                            Network Metrics
                        </Command.Item>
                    </Command.Group>

                </Command.List>
            </div>
        </Command.Dialog>
    );
};