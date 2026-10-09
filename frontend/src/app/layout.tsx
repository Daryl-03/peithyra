import type { Metadata } from "next";
import { Geist, Geist_Mono, Newsreader, Inter } from "next/font/google";
import "./globals.css";
import { cn } from "@/lib/utils";
import { Header } from "@/components/layout/header";

const inter = Inter({ subsets: ["latin"], variable: "--font-sans" });

const geistSans = Geist({
    variable: "--font-geist-sans",
    subsets: ["latin"],
});

const geistMono = Geist_Mono({
    variable: "--font-geist-mono",
    subsets: ["latin"],
});

const newsreader = Newsreader({
    variable: "--font-newsreader",
    subsets: ["latin"],
});

export const metadata: Metadata = {
    title: "Peithyra",
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html
            lang="en"
            className={cn(
                "h-full",
                "antialiased",
                geistSans.variable,
                geistMono.variable,
                newsreader.variable,
                "font-sans",
                inter.variable,
            )}
        >
            <body className="min-h-screen flex flex-col flex-1 items-center max-w-6xl xl:max-w-7xl mx-auto w-full justify-center">
                <Header />

                {children}

                <footer className="w-full">
                    <div className="flex items-center justify-between p-8 border-t border-t-primary/10 py-4">
                        <p className="text-muted-foreground text-sm">
                            Peithyra &copy; {new Date().getFullYear()} - Tous
                            droits réservés
                        </p>
                    </div>
                </footer>
            </body>
        </html>
    );
}
