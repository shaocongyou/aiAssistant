import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import JournalEntryService from './journal-entry.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { type IJournalEntry, JournalEntry } from '@/shared/model/journal-entry.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'JournalEntryUpdate',
  setup() {
    const journalEntryService = inject('journalEntryService', () => new JournalEntryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const journalEntry: Ref<IJournalEntry> = ref(new JournalEntry());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveJournalEntry = async journalEntryId => {
      try {
        const res = await journalEntryService().find(journalEntryId);
        res.createdAt = new Date(res.createdAt);
        journalEntry.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.journalEntryId) {
      retrieveJournalEntry(route.params.journalEntryId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required('本字段不能为空.'),
        maxLength: validations.maxLength('本字段最大长度为 100 个字符.', 100),
      },
      content: {
        required: validations.required('本字段不能为空.'),
      },
      mood: {
        maxLength: validations.maxLength('本字段最大长度为 50 个字符.', 50),
      },
      createdAt: {
        required: validations.required('本字段不能为空.'),
      },
      user: {},
    };
    const v$ = useVuelidate(validationRules, journalEntry as any);
    v$.value.$validate();

    return {
      journalEntryService,
      alertService,
      journalEntry,
      previousState,
      isSaving,
      currentLanguage,
      users,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: journalEntry }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.journalEntry.id) {
        this.journalEntryService()
          .update(this.journalEntry)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A JournalEntry is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.journalEntryService()
          .create(this.journalEntry)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A JournalEntry is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
