import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import JournalEntryService from './journal-entry.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IJournalEntry } from '@/shared/model/journal-entry.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'JournalEntryDetails',
  setup() {
    const dateFormat = useDateFormat();
    const journalEntryService = inject('journalEntryService', () => new JournalEntryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const journalEntry: Ref<IJournalEntry> = ref({});

    const retrieveJournalEntry = async journalEntryId => {
      try {
        const res = await journalEntryService().find(journalEntryId);
        journalEntry.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.journalEntryId) {
      retrieveJournalEntry(route.params.journalEntryId);
    }

    return {
      ...dateFormat,
      alertService,
      journalEntry,

      ...dataUtils,

      previousState,
    };
  },
});
