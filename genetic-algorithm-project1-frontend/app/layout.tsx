import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Genetic Algorithm Project 1",
  description: "Facilities Layout Problem is a NP-hard optimization problem that focuses on the optimal arrangement of the facilities in the given space. The project aims to minimize the total transportation cost that occur between facilities while considering constraints such as spatial limitations and facilities dimensions.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={``}
      >
        {children}
      </body>
    </html>
  );
}
