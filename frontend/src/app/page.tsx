import Image from "next/image";
import { Button } from "@/components/ui/button";
import { Dot, MoveRight } from "lucide-react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";

export default function Home() {
	return (
		<div className="flex flex-col flex-1 items-center max-w-6xl xl:max-w-7xl mx-auto w-full justify-center">
			<header className="w-full">
				<nav className="flex items-center justify-between p-8 border-b border-b-primary/10 py-4">
					<h1 className="text-2xl font-bold">Peithyra</h1>
					<div className="flex gap-4">
						<Button variant="ghost" size="lg">
							Sign In
						</Button>
						<Button variant="default" size="lg">
							Create Account
						</Button>
					</div>
				</nav>
			</header>
			<main className="flex-1 py-10 px-8 w-full flex flex-col gap-16">
				<section className="flex flex-col gap-2 w-1/2 items-start">
					<p className="uppercase text-muted-foreground  ">
						Une proposition. Deux camps.
					</p>
					<h1 className="text-5xl font-medium leading-tight mt-2 mb-4">
						Vos convictions méritent une vraie confrontation.
					</h1>
					<p className="text-muted-foreground  ">
						Défendez ou contestez une proposition, à deux et à tour
						de rôle. Ou lisez les échanges pour vous faire votre
						propre opinion.
					</p>

					<Button variant="default" size="lg" className="mt-4">
						Lancer une proposition
						<MoveRight />
					</Button>
				</section>

				<section  >
					<h2 className="text-3xl font-medium leading-tight mt-2 mb-4">
						Les propositions du moment
					</h2>

					<Tabs defaultValue="tous" >
						<div className="border-b w-full border-b-primary/10">
							<TabsList variant="line" className={"border-b lg:gap-10 border-b-primary/10 "} >
								<TabsTrigger value="tous">Tous</TabsTrigger>
								<TabsTrigger value="analytics">
									A rejoindre
								</TabsTrigger>
								<TabsTrigger value="en_cours">En cours</TabsTrigger>
								<TabsTrigger value="reports">Terminés</TabsTrigger>
							</TabsList>
						</div>

						<TabsContent value="tous">
							<div className="flex flex-col gap-4">
								{ [...Array(2)].map((_, index) => (
									<article key={index} className="flex flex-col gap-2 py-4 border-b border-b-primary/10">
									<p className="text-muted-foreground uppercase">
										Cherche une opposition
									</p>
									<h3 className="text-2xl font-medium leading-tight mt-2 mb-4">
										Le réchauffement climatique est une menace pour l'humanité
									</h3>

									<div className="flex justify-between flex-col sm:flex-row sm:items-center gap-4">
										<div className="flex gap-4 items-center">
											<div className="flex gap-2">
												<Badge variant="for">Pour</Badge>
												<span>Sarah</span>
											</div>
											<MoveRight />
											<div className="flex gap-2">
												<Badge variant="against">Contre</Badge>
												<span>???</span>
											</div>
										</div>
										<Button variant="secondary" size="lg">
											Voir la proposition
											<MoveRight />
										</Button>
									</div>
								</article>))
								}
								<article className="flex flex-col gap-2 py-4 border-b border-b-primary/10">
									<p className="text-muted-foreground uppercase flex items-center">
										<Dot color="green" />
										En cours
										-
										Tour 2 / 6
									</p>
									<h3 className="text-2xl font-medium leading-tight mt-2 mb-4">
										Le réchauffement climatique est une menace pour l'humanité
									</h3>

									<div className="flex justify-between flex-col sm:flex-row sm:items-center gap-4">
										<div className="flex gap-4 items-center">
											<div className="flex gap-2">
												<Badge variant="for">Pour</Badge>
												<span>Sarah</span>
											</div>
											<MoveRight />
											<div className="flex gap-2">
												<Badge variant="against">Contre</Badge>
												<span>???</span>
											</div>
										</div>
										<Button variant="secondary" size="lg">
											Voir la proposition
											<MoveRight />
										</Button>
									</div>
								</article>
								
							</div>
						</TabsContent>
						<TabsContent value="analytics">
							A rejoindre
						</TabsContent>
						<TabsContent value="en_cours">
							En cours
						</TabsContent>
						<TabsContent value="reports">
							Terminés
						</TabsContent>
					</Tabs>
				</section>
			</main>

			<footer className="w-full">
				<div className="flex items-center justify-between p-8 border-t border-t-primary/10 py-4">
					<p className="text-muted-foreground text-sm">
						Peithyra &copy; {new Date().getFullYear()} - Tous droits réservés
					</p>
				</div>
			</footer>
		</div>
	);
}
