"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { LogoutLink } from "@kinde-oss/kinde-auth-nextjs/components";
import { ArrowRight, LoaderCircle } from "lucide-react";
import { useAction } from "next-safe-action/hooks";
import { Controller, useForm } from "react-hook-form";
import type { z } from "zod";
import { Button } from "@/components/ui/button";
import {
    Field,
    FieldError,
    FieldGroup,
    FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { createProfile } from "../actions/create-profile";
import { onboardingFormSchema } from "../model/onboarding-schema";

export function OnboardingForm() {
    const form = useForm<z.infer<typeof onboardingFormSchema>>({
        resolver: zodResolver(onboardingFormSchema),
        defaultValues: { username: "" },
        mode: "onBlur",
    });
    const username = form.watch("username");
    const { executeAsync, isPending } = useAction(createProfile, {
        onError: ({ error }) => {
            if (error.validationErrors) {
                let shouldFocus = true;
                for (const name of onboardingFormSchema.keyof().options) {
                    const message = error.validationErrors[name]?._errors?.[0];
                    if (message) {
                        form.setError(
                            name,
                            { type: "server", message },
                            { shouldFocus },
                        );
                        shouldFocus = false;
                    }
                }
                const message = error.validationErrors._errors?.[0];
                if (message)
                    form.setError("root.server", { type: "server", message });
                return;
            }

            form.setError("root.server", {
                type: "server",
                message:
                    error.serverError ??
                    "Impossible de joindre le serveur. Veuillez réessayer.",
            });
        },
    });
    const pending = form.formState.isSubmitting || isPending;

    async function onSubmit(data: z.infer<typeof onboardingFormSchema>) {
        form.clearErrors("root");
        await executeAsync(data);
    }

    return (
        <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="mt-8 space-y-6"
            aria-busy={pending}
        >
            <div className="flex items-center gap-4 rounded-lg border bg-card p-4">
                <div
                    aria-hidden="true"
                    className="flex size-12 shrink-0 items-center justify-center rounded-full bg-secondary text-lg font-medium text-secondary-foreground"
                >
                    {username.slice(0, 2).toLocaleUpperCase("fr") || "P"}
                </div>
                <div className="min-w-0">
                    <p className="wrap-break-word text-sm font-medium">
                        {username || "Votre pseudo"}
                    </p>
                    <p className="mt-1 text-xs text-muted-foreground">
                        Votre identité dans les débats
                    </p>
                </div>
            </div>

            <div className="space-y-2">
                <FieldGroup>
                    <Controller
                        name="username"
                        control={form.control}
                        render={({ field, fieldState }) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="username">
                                    Votre pseudo
                                </FieldLabel>
                                <Input
                                    {...field}
                                    className="h-12 w-full rounded-md border border-input bg-card px-4 text-base outline-none transition-shadow placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/20 aria-invalid:border-destructive"
                                    id="username"
                                    aria-invalid={fieldState.invalid}
                                    aria-describedby={
                                        fieldState.invalid
                                            ? "username-error"
                                            : undefined
                                    }
                                    readOnly={pending}
                                    placeholder="ex. Camille"
                                    autoComplete="off"
                                    required
                                />
                                {fieldState.invalid && (
                                    <FieldError
                                        id="username-error"
                                        errors={[fieldState.error]}
                                    />
                                )}
                            </Field>
                        )}
                    />
                </FieldGroup>

                {/* <p id="username-hint" className="text-xs text-muted-foreground">
                    Au moins 5 caractères. Choisissez un pseudo plutôt que votre
                    email.
                </p> */}
            </div>

            {form.formState.errors.root?.server && (
                <FieldError errors={[form.formState.errors.root.server]} />
            )}

            <Button
                type="submit"
                size="lg"
                disabled={pending}
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
