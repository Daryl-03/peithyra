"use client";

import { LogoutLink } from "@kinde-oss/kinde-auth-nextjs/components";
import { ArrowRight, LoaderCircle } from "lucide-react";
import { useActionState, useState } from "react";
import { Button } from "@/components/ui/button";
import {
	createProfile,
	CreateProfileActionResult,
} from "../actions/create-profile";
import { z } from "zod";
import { onboardingFormSchema } from "../model/onboarding-schema";

const initialState: CreateProfileActionResult = {};

export function OnboardingForm() {
	const [state, action, pending] = useActionState(
		createProfile,
		initialState,
	);
	const [username, setUsername] = useState("");
	const [touched, setTouched] = useState(false);

	const trimmed = username.trim();
	const validation = onboardingFormSchema.safeParse({ username: trimmed });
	const valid = validation.success;

	const clientError = touched && !valid ? validation.error.issues.find((issue) => issue.path[0] === "username")?.message : undefined;

	const businessError =
		state.data?.username === trimmed ? state.data.error : undefined;

	const schemaError = state.validationErrors?.username?._errors?.[0];

	const error = clientError ?? schemaError ?? businessError ?? state.serverError;

	return (
		<form action={action} className="mt-8 space-y-6" aria-busy={pending}>
			<div className="flex items-center gap-4 rounded-lg border bg-card p-4">
				<div
					aria-hidden="true"
					className="flex size-12 shrink-0 items-center justify-center rounded-full bg-secondary text-lg font-medium text-secondary-foreground"
				>
					{trimmed.slice(0, 2).toLocaleUpperCase("fr") || "P"}
				</div>
				<div className="min-w-0">
					<p className="wrap-break-word text-sm font-medium">
						{trimmed || "Votre pseudo"}
					</p>
					<p className="mt-1 text-xs text-muted-foreground">
						Votre identité dans les débats
					</p>
				</div>
			</div>
			<div className="space-y-2">
				<label htmlFor="username" className="text-sm font-medium">
					Votre pseudo
				</label>
				<input
					id="username"
					name="username"
					value={username}
					onChange={(event) => setUsername(event.target.value)}
					onBlur={() => setTouched(true)}
					required
					minLength={5}
					maxLength={50}
					autoComplete="username"
					autoCapitalize="none"
					spellCheck={false}
					readOnly={pending}
					placeholder="ex. Camille"
					aria-invalid={Boolean(error)}
					aria-describedby={
						error ? "username-hint username-error" : "username-hint"
					}
					className="h-12 w-full rounded-md border border-input bg-card px-4 text-base outline-none transition-shadow placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/20 aria-invalid:border-destructive"
				/>
				<p id="username-hint" className="text-xs text-muted-foreground">
					Au moins 5 caractères. Choisissez un pseudo plutôt que votre
					email.
				</p>
				<div aria-live="polite" aria-atomic="true">
					{error && (
						<p
							id="username-error"
							className="text-sm text-destructive"
						>
							{error}
						</p>
					)}
				</div>
			</div>
			<Button
				type="submit"
				size="lg"
				disabled={pending || !valid}
				className="h-12 w-full"
			>
				{pending ? (
					<>
						<LoaderCircle
							aria-hidden="true"
							className="animate-spin"
						/>
						Création du profil…
					</>
				) : (
					<>
						Rejoindre Peithyra
						<ArrowRight aria-hidden="true" />
					</>
				)}
			</Button>
			<p className="text-center text-sm text-muted-foreground">
				Ce n’est pas votre compte ?{" "}
				<LogoutLink className="text-foreground underline underline-offset-4">
					Se déconnecter
				</LogoutLink>
			</p>
		</form>
	);
}
